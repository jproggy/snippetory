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

public class ParseError extends SnippetoryException {
	private static final long serialVersionUID = 1L;

	public ParseError(String message, Token at) {
		super(message + "  " + toMessage(at));
	}

	public ParseError(Throwable cause, Token at) {
		super(toMessage(at), cause);
	}

	private static String toMessage(Token at) {
		if (at == null) return "Error at end";
		return "Error while parsing " + at.getContent() + " at position " + at.getPosition();
	}
}
