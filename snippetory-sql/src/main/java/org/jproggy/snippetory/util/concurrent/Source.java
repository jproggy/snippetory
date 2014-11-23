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
