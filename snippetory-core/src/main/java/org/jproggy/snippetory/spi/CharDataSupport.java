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

import org.jproggy.snippetory.Encodings;

/**
 * The Snippetory templating solution works with two different form of character data:
 * <ol>
 *   <li> {@link CharSequence} is provided by the JDK and as such well known. </li>
 *   <li>
 *     But ths latter one is not able to carry along the encoding. This done with
 *     {@link EncodedData}.
 *   </li>
 * </ol>
 * This class is inteded to handle both types in a common way.
 *
 * @author B. Ebertz
 *
 */
public class CharDataSupport {
  /**
   * Determine whether this object is one of the supported types.
   */
  public static boolean isCharData(Object value) {
    return value instanceof EncodedData || value instanceof CharSequence;
  }

  /**
   * Calculate the length of the character data. This method may only be called with
   * character data.
   */
  public static int length(Object chars) {
    return toCharSequence(chars).length();
  }

  /**
   *  Converts the Object to <code>CharSequence</code>. This method may only be called with
   * character data.
   */
  public static CharSequence toCharSequence(Object chars) {
    if (chars instanceof CharSequence) {
      return (CharSequence)chars;
    }
    if (chars instanceof EncodedData) {
      return ((EncodedData)chars).toCharSequence();
    }
    return chars.toString();
  }

  /**
   * Figures out the encoding of this data. Will assume plain text
   * if no encoding is provided.
   * This method may only be called with character data.
   */
  public static String getEncoding(Object chars) {
    if (chars instanceof EncodedData) {
      ((EncodedData)chars).getEncoding();
    }
    return Encodings.plain.getName();
  }
}
