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

package org.jproggy.snippetory.engine;

import java.util.Set;

public interface DataSink {

  /**
   *  <p>
   * Offers a value to the callee.  The callee is responsible for filtering out the
   * relevant names. Thus it's expected to ignore all names, that aren't listed in
   * it's names(). However, the caller may do this filtering, too, as far as it provides
   * a names declared by the names() method. The set method is intended to keep
   * only a single value.
   * </p>
   */
  void set(String name, Object value);

  /**
   *  <p>
   * Appends a String representation of the value to all variables with given name.
   * The exact value might differ according to different meta data associated with
   * each of these variables. Eventually set or appended data is kept and new data
   * is appended behind the last character.
   * </p>
   *  <p>
   * All matching formats and encodings are used. However, there is some
   * special handling for the interface (@link EncodedData). In this case the
   * provided encoding in determined to calculate the correct transcoding.
   * </p>
   */
  void append(String name, Object value);

  /**
   * Declares the names supported by this DataSink. The returned Set must not
   * change over time. It may return different instances, but it must be reliable
   * to cache the result.
   */
  Set<String> names();

  /**
   * Declares the names supported by this DataSink. The returned Set must not
   * change over time. It may return different instances, but it must be reliable
   * to cache the result.
   */
  Set<String> regionNames();

  /**
   * Create a new, clean instance. The returned instance has to be decoupled
   * in that status changes are not reflected on the called instance. Thus,
   * immutable instances may return themselves.
   */
  DataSink cleanCopy(Location parent);

  /**
   * Reset to same state as directly after parsing the template.
   */
  void clear();

  /**
   * The data of this sink represented as a char sequence including already set parameters.
   * If the sink implements {@link CharSequence} it might just return {@code this} for sake of performance.
   */
  CharSequence format();

  /**
   * Acquire the sub-template selected by its name
   */
  Reference getChild(String name);
}
