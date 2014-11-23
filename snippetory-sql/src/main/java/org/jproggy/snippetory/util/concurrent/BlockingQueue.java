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