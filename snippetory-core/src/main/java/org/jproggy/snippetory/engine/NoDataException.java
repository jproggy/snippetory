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

import java.util.Collections;
import java.util.List;

public class NoDataException extends SnippetoryException {
  private static final long serialVersionUID = 1L;

  private final List<Exception> exceptions;

  public NoDataException(String message, List<Exception> exceptions) {
    super(message, first(exceptions));
    this.exceptions = exceptions;
  }

  public NoDataException(String message) {
    super(message);
    this.exceptions = Collections.emptyList();
  }

  public NoDataException(Throwable cause) {
    super(cause);
    this.exceptions = Collections.emptyList();
  }

  public List<Exception> getExceptions() {
    return exceptions;
  }

  private static Exception first(List<Exception> exceptions) {
    return exceptions.isEmpty() ? null : exceptions.get(0);
  }
}
