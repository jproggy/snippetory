package org.jproggy.snippetory.util.concurrent;

import java.io.Closeable;

public interface Sink<E> extends Closeable {
  /**
   * Inserts the specified element at the tail of this queue, waiting
   * for space to become available if the queue is full.
   *
   * @throws InterruptedException {@inheritDoc}
   * @throws NullPointerException {@inheritDoc}
   */
  public void put(E e) throws InterruptedException, QueueCloseException;

  @Override
  public void close();
}
