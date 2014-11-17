package org.jproggy.snippetory.util.concurrent;

import java.io.Closeable;
import java.util.Iterator;

/**
 *
 */
public interface Source<T> extends Iterable<T>, Closeable {

  /**
   * Provides a blocking iterator, that will wait in {@code hasNext}
   * until data is available, the associated {@link BlockingQueue} is
   * closed or the thread is interrupted. To make sure next can provide
   * any data, this data has to be reserved by the hasNext method.
   * Thus omitting to call next when hasNext returned true will queue in
   * loss of data.
   * Using the for each loop can guarantee correct usage easily.
   *
   * Note: The iterator is merely designed for he for each loop.
   * <strong>There must not be more than one iterator per
   */
  @Override
  public Iterator<T> iterator();

  /**
   *
   */
  @Override
  public void close();
}
