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

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A bounded {@linkplain BlockingQueue blocking queue} backed by an
 * array.  This queue orders elements FIFO (first-in-first-out).
 */
public class ArrayBlockingQueue<E> implements BlockingQueue<E> {
  private static final long NO_TIMEOUT = -1l;
  private final Object[] data;
  /** number of items currently in the queue waiting to be taken */
  private int usage;
  /**
   * if the queue will not put additional items or allow sinks to be created,
   * but it's still possible to take data until queue is empty and to register
   * further sources.
   */
  private boolean closed = false;
  private final ReentrantLock lock;
  private long taken;

  /** refers to next item to be taken in {@code data} */
  private int srcPointer;
  /** number of active sources, that are registered at given point in time */
  private int sources;
  /** used to wait if queue is full and to signal, that data was taken */
  private final Condition notFull;

  /** refers to next item to be put in {@code data} */
  private int sinkPointer;
  /** number of active sinks, that are registered at given point in time */
  private int sinks;
  /** used to wait if queue is empty and to signal, that data was put */
  private final Condition notEmpty;

  private final long regPhaseEnd;
  private long blockedSince;

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
   * capacity and default access policy. The time out is used to wait for
   * the first sink to register. The timeout will be used by the
   * {@code take}-method to wait for the first sink to register.
   * If no
   *
   * @param capacity the capacity of this queue
   * @throws IllegalArgumentException if {@code capacity < 1}
   */
  public ArrayBlockingQueue(int capacity, long timeout, TimeUnit unit) {
    this(capacity, false, timeout, unit);
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
    this(capacity, fair, NO_TIMEOUT, TimeUnit.NANOSECONDS);
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
  public ArrayBlockingQueue(int capacity, boolean fair, long timeout, TimeUnit unit) {
    if (capacity <= 0) throw new IllegalArgumentException();
    this.data = new Object[capacity];
    lock = new ReentrantLock(fair);
    notEmpty = lock.newCondition();
    notFull = lock.newCondition();
    if (timeout != NO_TIMEOUT) {
      this.regPhaseEnd = System.nanoTime() + unit.toNanos(timeout);
    } else {
      this.regPhaseEnd = NO_TIMEOUT;
    }
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
    data[sinkPointer] = Objects.requireNonNull(x);
    sinkPointer = inc(sinkPointer);
    if (++usage == data.length) blockedSince = System.currentTimeMillis();
    notEmpty.signal();
  }

  private void put(E e) throws InterruptedException, QueueClosedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      if (closed) throw new QueueClosedException();
      while (usage == data.length) {
        notFull.await();
        if (closed) throw new QueueClosedException();
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
    E x = (E)(items[srcPointer]);
    items[srcPointer] = null;
    srcPointer = inc(srcPointer);
    if (--usage == 0) blockedSince = System.currentTimeMillis();
    ++taken;
    notFull.signal();
    return x;
  }

  @Override
  public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      if (isVirgin()) {
        long waitTime = waitNanos();
        while (waitTime > 0 && notEmpty.awaitNanos(waitTime) <= 0) {
          if (usage > 0) return extract();
          waitTime  =  waitNanos();
        }
      }
      if (sinks == 0 && regPhaseEnd != NO_TIMEOUT) shutDown();
      while (usage == 0) {
        if (closed) return null;
        notEmpty.await();
      }
      return extract();
    } finally {
      lock.unlock();
    }
  }

  private long waitNanos() {
    return regPhaseEnd - System.nanoTime();
  }

  /**
   *
   */
  private boolean isVirgin() {
    return sinks == 0 && !closed && taken == 0 && usage == 0;
  }

  @Override
  public void consume(Consumer<E> consumer) throws InterruptedException {
    try (Source<E> src = source()) {
      for (E item : src) {
        consumer.accept(item);
      }
      if (Thread.interrupted()) throw new InterruptedException();
    }
  }

  @Override
  public long length() {
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
  public Date blockedSince() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (usage == 0 || usage == data.length) {
        return new Date(blockedSince);
      }
      return null;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public long numberOfSinks() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return sinks;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public long numberOfSources() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return sources;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close(boolean immediately) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (immediately) shutDownNow(); else shutDown();
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
    if (closed) return;
    closed = true;
    notEmpty.signalAll();
  }

  private void shutDownNow() {
    if (closed && usage == 0) return;
    closed = true;
    usage = 0;
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
      for (int i = srcPointer;; i = inc(i)) {
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
        throw new QueueClosedException();
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
    public void put(E e) throws InterruptedException, QueueClosedException {
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
          if (!(isVirgin() && waitNanos() > 0)){
            ArrayBlockingQueue.this.shutDown();
          }
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
        if (ArrayBlockingQueue.this.closed && usage == 0) {
          throw new QueueClosedException();
        }
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
