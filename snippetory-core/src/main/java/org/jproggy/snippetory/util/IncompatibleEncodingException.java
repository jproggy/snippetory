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

package org.jproggy.snippetory.util;

import org.jproggy.snippetory.SnippetoryException;
import org.jproggy.snippetory.spi.Transcoding;

/**
 * Transcoding is not supported. This can be solved by registering a matching {@link Transcoding}
 */
public class IncompatibleEncodingException extends SnippetoryException {
  private static final long serialVersionUID = 1L;

  public IncompatibleEncodingException(String message) {
    super(message);
  }
}
