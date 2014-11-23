/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

package org.jproggy.snippetory.util.concurrent;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A bounded {@linkplain BlockingQueue blocking queue} backed by an
 * array.  This queue orders elements FIFO (first-in-first-out).
 *
 * Despite the similarly named classes in the JDK this queue is designed
 * for simple usage in an Producer / Consumer context. The Producer
 * interacts through a Sink with the queue. The idiom is like this:
 *
 * <pre>
 * try (Sink<E> sink = queue.sink()) {
 *   sink.put(produceEs())
 * } catch (InterruptedException e) {
 *   Thread.currentThread().interrupt();
 * } catch (QueueCloseException e) {
 *   // typically no handling necessary
 * }
 * </pre>
 *
 * The consumer uses a sink like this:
 *
 * <pre>
 *  try (Source<E> s = queue.source()) {
 *    for (E item : s) {
 *      consume(item);
 *    }
 *  }
 * </pre>
 *
 * or on JDK8 this can be short cut even
 *
 * <pre>
 *   queue.consume(item -> consume(item));
 * </pre>
 *
 * Once all sinks or all sources have been shut down the queue will be closed, too.
 * Once a queue is closed all the sink will refuse calls to the put-method by throwing
 * a {@link QueueCloseException}. While the active sources will finish to provide already
 * produced items and then end normally by returning {@code false} on {@code hasNext()}.
 *
 * The queue can be centrally shut down by the close method, providing a controlled way to
 * end the entire Produce / Consume process.
 *
 * There's a counter for already taken item for convenient through put measurement as well
 * as general monitoring of the process.
 *
 * Note: {@code Sink}s and {@code Source}s are not thread safe and thus must no be used by
 * concurrent threads. Of course the queue is designed for multi-threading and each Source
 * and each Sink can be used by a different thread.
 */
public class ArrayBlockingQueue<E> implements BlockingQueue<E> {
  private final Object[] data;
  private int usage;
  private boolean closed = false;
  private final ReentrantLock lock;
  private long taken;

  private int srcIndex;
  private int sources;
  private final Condition notFull;

  private int sinkIndex;
  private int sinks;
  private final Condition notEmpty;

  private int inc(int i) {
    return (++i >= data.length) ? 0 : i;
  }

  /**
   * Creates an {@code ArrayBlockingQueue} with the given (fixed)
   * capacity and default access policy.
   *
   * @param capacity the capacity of this queue
   * @throws IllegalArgumentException if {@code capacity < 1}
   */
  public ArrayBlockingQueue(int capacity) {
    this(capacity, false);
  }

  /**
   * Creates an {@code ArrayBlockingQueue} with the given (fixed)
   * capacity and the specified access policy.
   *
   * @param capacity the capacity of this queue
   * @param fair if {@code true} then queue accesses for threads blocked
   *        on insertion or removal, are processed in FIFO order;
   *        if {@code false} the access order is unspecified.
   * @throws IllegalArgumentException if {@code capacity < 1}
   */
  public ArrayBlockingQueue(int capacity, boolean fair) {
    if (capacity <= 0) throw new IllegalArgumentException();
    this.data = new Object[capacity];
    lock = new ReentrantLock(fair);
    notEmpty = lock.newCondition();
    notFull = lock.newCondition();
  }

  @Override
  public Sink<E> sink() {
    return new SinkImpl();
  }

  /**
   * Inserts element at current put position, advances, and signals.
   * Call only when holding lock.
   */
  private void insert(E x) {
    data[sinkIndex] = Objects.requireNonNull(x);
    sinkIndex = inc(sinkIndex);
    ++usage;
    notEmpty.signal();
  }

  private void put(E e) throws InterruptedException, QueueCloseException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      if (closed) throw new QueueCloseException();
      while (usage == data.length) {
        notFull.await();
        if (closed) throw new QueueCloseException();
      }
      insert(e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Source<E> source() {
    return new SourceImpl();
  }

  /**
   * Extracts element at current take position, advances, and signals.
   * Call only when holding lock.
   */
  private E extract() {
    final Object[] items = this.data;
    @SuppressWarnings("unchecked")
    E x = (E)(items[srcIndex]);
    items[srcIndex] = null;
    srcIndex = inc(srcIndex);
    --usage;
    ++taken;
    notFull.signal();
    return x;
  }

  @Override
  public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      while (usage == 0) {
        if (closed) return null;
        notEmpty.await();
      }
      return extract();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void consume(Consumer<E> consumer) {
    try (Source<E> src = source()) {
      for (E item : src) {
        consumer.consume(item);
      }
    }
  }

  @Override
  public long capacity() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return data.length;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public long usage() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return usage;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public long taken() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return taken;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      shutDown();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean isClosed() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return closed;
    } finally {
      lock.unlock();
    }
  }

  private void shutDown() {
    closed = true;
    notEmpty.signalAll();
  }

  @Override
  public String toString() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (usage == 0) return "[]";

      int count = usage;
      StringBuilder out = new StringBuilder();
      out.append('[');
      for (int i = srcIndex;; i = inc(i)) {
        Object e = data[i];
        out.append(e == this ? "(this Queue)" : e);
        if (--count == 0) return out.append(']').toString();
        out.append(',').append(' ');
      }
    } finally {
      lock.unlock();
    }
  }

  private class SinkImpl implements Sink<E> {
    private boolean closed;
    public SinkImpl() {
      if (ArrayBlockingQueue.this.isClosed()) {
        throw new QueueCloseException();
      }
      final ReentrantLock lock = ArrayBlockingQueue.this.lock;
      lock.lock();
      try {
        ++ArrayBlockingQueue.this.sinks;
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void put(E e) throws InterruptedException, QueueCloseException {
      if (closed) throw new IllegalStateException("This sink is already closed");
      ArrayBlockingQueue.this.put(e);
    }

    @Override
    public void close() {
      final ReentrantLock lock = ArrayBlockingQueue.this.lock;
      lock.lock();
      try {
        if (closed) return;
        --ArrayBlockingQueue.this.sinks;
        if (ArrayBlockingQueue.this.sinks <= 0) {
          ArrayBlockingQueue.this.shutDown();
          closed = true;
        }
      } finally {
        lock.unlock();
      }
    }
  }

  private class SourceImpl implements Source<E> {
    private boolean closed = false;
    public SourceImpl() {
      final ReentrantLock lock = ArrayBlockingQueue.this.lock;
      lock.lock();
      try {
        ++sources;
      } finally {
        lock.unlock();
      }
    }

    @Override
    public Iterator<E> iterator() {
      if (closed) throw new IllegalStateException("already closed");
      return new Itr();
    }

    @Override
    public void close() {
      final ReentrantLock lock = ArrayBlockingQueue.this.lock;
      lock.lock();
      try {
        if (closed) return;
        closed = true;
        --sources;
        if (sources == 0) {
          ArrayBlockingQueue.this.shutDown();
        }
      } finally {
        lock.unlock();
      }
    }

    private class Itr implements Iterator<E> {
      private E nextItem;    // Element to be returned by next call to next

      @Override
      public boolean hasNext() {
        try {
          if (nextItem == null) {
            // let it finish a started process
            nextItem = take();
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return false;
        }
        return nextItem != null;
      }

      @Override
      public E next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        E e = nextItem;
        nextItem = null;
        return e;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }
}
