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

/**
 * A bounded blocking queue, designed explicitly for Producer / 
 * Consumer scenarios. It features a clear shut down handling and
 * a central super vision interface for watchdog and monitoring purposes.
 * Producers register by creating a sink and unregister by closing the
 * sink. Consumers register by creating a source and unregister by
 * closing the source.
 * This queue orders elements FIFO (first-in-first-out).
 *
 * Despite the similarly named classes in the JDK this queue is <em>not</em> a 
 * {@link java.util.Collection collection} but provides registration mechanism
 * for producers and consumers. The producer
 * interacts through a {@code Sink} with the queue. The idiom is like this:
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
 *   // typically no handling necessary
 * }
 * </pre>
 *
 * <p>The consumer uses a sink like this:
 *
 * <pre>
 *  {@code try (Source<E> s = queue.source())} {
 *    for (E item : s) {
 *      consume(item);
 *    }
 *  }
 * </pre>
 *
 * <p>or on JDK8 this can be short cut even
 *
 * <pre>
 *   {@code queue.consume(item -> consume(item));}
 * </pre>
 *
 * <p>The queue can be centrally shut down by the close method, providing a controlled way to
 * end the entire Produce / Consume process.
 *
 * <strong>
 * Note: If the registration of all Sinks fails (because of an exception or the like)
 * all registered Source will wait for ever for data to be sent (aka be deadlocked).
 * On the other hand if the first producer fails (and unregisters the Sink) before
 * the second producer is registered this might undesired shut down the whole queue.
 * Be sure to make the source the first resource in the try-with-resources statement. If no sink gets
 * registered, the sources are deadlocked. This can be handled by a timeout, but the risk should be reduced as
 * far as possible!
 * </strong>
 *
 * <p>There's a counter for already taken item for convenient through put measurement as well
 * as general monitoring of the process.
 *
 * <p>The queue will then not allow to put additional items
 * or to create additional sinks, but it's still possible to take data until queue is empty
 * and to register further sources.
 *
 * Once all sinks or all sources have been shut down the queue will be closed, too.
 * Once a queue is closed all the sink will refuse calls to the put-method by throwing
 * a {@link QueueClosedException}. While the active sources will finish to provide already
 * produced items and then end normally by returning {@code false} on {@code hasNext()}.
 *
 * Note: {@code Sink}s and {@code Source}s are not thread safe and thus must no be used by
 * concurrent threads. Of course the queue is designed for multi-threading and each Source
 * and each Sink can be used by a different thread.
 *
 * The monitoring interface provides some data about the state and performance of the queue.
 * This makes it easy to supervise and control the queue by a watchdog or, through MBeans
 * with standard IT monitoring tools.
 */
public interface BlockingQueue<E> {

  /**
   * Provides blocking access to the next item. A next item is expected as long as the
   * queue is open, or contains more items. If there gets no {@code Source} registered the
   * queue will block forever, or have a timeout to avoid this.
   *
   * @return the next item in the queue or {@code null} if no further item is expected
   */
  public E take() throws InterruptedException;

  /**
   * Will span a new {@link Source} and offer all items provided by this source to the consumer.
   * This method is intended to be called simultaneously by different threads to build several
   * parallel consumers. Each item will then be offered to one and only one of those consumers.
   * <p>This call will return when the queue is closed and empty, the consumer throws an exception,
   * or the thread is interrupted
   */
  public void consume(Consumer<E> consumer) throws InterruptedException, QueueClosedException;

  /**
   * Creates a new sink, that allows a producer to be registered with the queue.
   * To unregister the producer has to close the sink.
   * @throws QueueClosedException if queue is already closed
   */
  public Sink<E> sink() throws QueueClosedException;

  /**
   * Creates a new source, that allows a consumer to be registered with the queue.
   * To unregister the consumer has to close the source.
   * @throws QueueClosedException if the queue is closed and empty
   */
  public Source<E> source() throws QueueClosedException;

  /**
   * Allows to close the the entire queue. All Sinks refuse the items offered
   * through the put method by throwing a {@link QueueClosedException}. It's
   * not possible to create new Sinks. Once the queue is empty all Sources
   * will stop waiting for additional data. Their {@link Iterator#hasNext}
   * method will return false, and {@link Iterator#next} will throw
   * {@link NoSuchElementException}. For the recommended idiom this mean
   * the consumers just finish.
   *
   * @param clear  true: all items are discarded. Sources are shut down immediately<br>
                   false: item in the queue are offered to the sources, so that the 
				   provided data will be processed.
   */
  public void close(boolean clear);

  /**
   * @return true is the queue is closed, false otherwise.
   */
  public boolean isClosed();

  /**
   * The number of items contained in this queue at the moment.
   * If it return 0 the queue is considered empty.
   *
   * <p>Note that you <em>cannot</em> always tell if an attempt to insert
   * an element will succeed by inspecting {@code usage}
   * because it may be the case that another thread is about to
   * insert or remove an element.
   */
  public long usage();

  /**
   * Number of elements that can be stored in the queue.
   */
  public long length();

  /**
   * Number of items, that are already been taken from that queue.
   */
  public long taken();

  /**
   * Allows to monitor blocking periods or write a watchdog to
   * supervise to queue.
   * @return null if queue is neither full nor empty, the date
   * when entered current state, otherwise.
   */
  public Date blockedSince();

  /**
   * Number of producers that are registered with this queue.
   * Once there are no more producers registered, that queue
   * will be shut down automatically if the registration phase
   * has already finished.
   */
  public long numberOfSinks();

  /**
   * Number of consumers that are registered with this queue.
   * Once there are no more consumers registered, that queue
   * will be shut down automatically if the registration phase
   * has already finished.
   */
  public long numberOfSources();
}