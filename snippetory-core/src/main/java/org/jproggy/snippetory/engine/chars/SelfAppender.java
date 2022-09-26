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

package org.jproggy.snippetory.engine.chars;

/**
 * Whenever one {@code CharSequence} is appended to another, it analyzes
 * the target type to figure out an efficient way. If the type of the
 * target is unknown single characters are read, which is pretty slow.
 * This method works as a workaround  for that design flaw.
 *
 * @author B. Ebertz
 */
public interface SelfAppender {
  /**
   * Append own data in chunks to the appendable
   */
  <T extends Appendable> T appendTo(T to);
}
