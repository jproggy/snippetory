package org.jproggy.snippetory.util.concurrent;


public interface BlockingQueue<E> {

  public E take() throws InterruptedException;

  public void consume(Consumer<E> consumer);

  public Sink<E> sink();

  public Source<E> source();

  /**
   * Returns the number of elements in this queue.
   *
   * @return the number of elements in this queue
   */
  public long usage();

  /**
   * Returns the number of additional elements that this queue can ideally
   * (in the absence of memory or resource constraints) accept without
   * blocking. This is always equal to the initial capacity of this queue
   * less the current {@code size} of this queue.
   *
   * <p>Note that you <em>cannot</em> always tell if an attempt to insert
   * an element will succeed by inspecting {@code usage}
   * because it may be the case that another thread is about to
   * insert or remove an element.
   */
  public long capacity();

  public long taken();

  public void close();

  public boolean isClosed();
}