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

package org.jproggy.snippetory.spi;

import org.jproggy.snippetory.engine.EncodingRegistry;

/**
 * Combines character data as pay load with information about it's encoding
 * as additional meta data.
 *
 * @author B. Ebertz
 *
 */
public interface EncodedData {
  /**
   * The encoding is represented by it's name. The name can be resolved
   * by  {@link EncodingRegistry#get(String) Encoding.REGISTRY.get(String)}.
   */
  String getEncoding();

  /**
   * Convert to a char sequence.
   * This methods is added, because a toString method might be more expensive
   * in many cases. This method allows to return a StringBuilder instead of a String.
   * This might avoid copying the data from the StringBuilder into a String.
   */
  CharSequence toCharSequence();
}
