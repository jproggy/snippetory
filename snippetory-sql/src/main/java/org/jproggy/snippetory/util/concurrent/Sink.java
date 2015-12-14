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

import java.io.Closeable;

/**
 * Provides a way how some can offer items to some kind of consumers on a high
 * level of abstraction. The idiom is like this:
 *
 * <pre>
 * {@code try (Sink<E> sink = queue.sink())} {
 *   while (hasMoreEs()) {
 *     sink.put(produceE())
 *   }
 * } catch (InterruptedException e) {
 *   // only if your method won't throw InterruptedException of course
 *   Thread.currentThread().interrupt();
 * } catch (QueueClosedException e) {
 *   // typically no handling necessary. Just shut down the producer.
 *   // The exception signals that the queue has been closed directly
 *   // or by closing all Sources i.e. Consumers
 * }
 * </pre>
 *
 * While a {@code Sink} isn't thread safe a thus must not be accessed by different
 * threads at the same time, there may be several {@code Sink}s of the same
 * queue used by it's own thread each.
 *
 * @param <E> type of the items handled by this queue
 */
public interface Sink<E> extends Closeable {
  /**
   * Inserts the specified item at the tail of this queue, waiting
   * for space to become available if the queue is full.
   *
   * @param e the item to add
   * @throws InterruptedException if interrupted while waiting
   * @throws ClassCastException if the class of the specified item
   *         prevents it from being added to this queue
   * @throws NullPointerException if the specified item is null
   * @throws IllegalArgumentException if some property of the specified
   *         item prevents it from being added to this queue
   */
  public void put(E e) throws InterruptedException, QueueClosedException;

  /**
   * If the usage of a source is canceled, make sure that close is called.
   * The simplest way to do so is the use of the try-with-resources statement.
   */
  @Override
  public void close();
}
