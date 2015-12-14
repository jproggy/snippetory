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

package org.jproggy.snippetory.sql;

import java.util.Iterator;

/**
 * High level abstraction for providing the results of some kind of a query translated to objects.
 * Those results might be completely or partially transformed before or are transformed on the fly
 * depending on the implementation. The fact the cursor is an {@code AutoCloseable} allows the cursor
 * to handle underlying resources safely.
 * <p> To be able handle the cursor with a try-with-resources statement the cursor needs to be consumed
 * where produced. Further more the cursor is designed for one time usage. It's not allowed to have more
 * than one iterator per {@code Cursor} instance.
 *
 * <p>The idiom is:
 * <pre>
 *   {@code try (Cursor<T> ts = query.cursor(toT()))} {
 *     // it's fine to do some stuff here even, even those that might avoid actual consumption
 *     // of the cursor.
 *     for (T t: ts) {
 *       consume(t); // whatever you like to do with it
 *     }
 *   }
 * </pre>
 *
 * @param <T> Type of the items provided by this cursor
 *
 * <em>Note: A cursor might keep underlying resources open until completely
 * consumed. Thus it is <b>not</b> intended to be <b>kept in an attribute</b>
 * but rather for direct consumption.</em>
 */
public interface Cursor<T> extends Iterable<T>, AutoCloseable {
  /**
   * A {@code Cursor} has a position by design, thus there can be only one
   * Iterator over the complete life time of the {@code Cursor}.
   * {@code Iterable} is only used for compatibility with the for each loop.
   */
  @Override
  public Iterator<T> iterator();

  @Override
  public void close();
}
