/*******************************************************************************
 * Copyright (c) 2011-2012 JProggy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE,
 * NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE
 *******************************************************************************/

package org.jproggy.snippetory.engine;

import java.util.Collections;
import java.util.List;


public class NoDataException extends SnippetoryException {
  private static final long serialVersionUID = 1L;

  private final List<Exception> exceptions;

  public NoDataException(String message, List<Exception> exceptions) {
    super(message);
    if (!exceptions.isEmpty()) this.initCause(exceptions.get(0));
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
}
