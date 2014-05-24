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

package org.jproggy.snippetory.spi;

/**
 * SyntaxID is typically implemented by an enum used to identify a named {@link Syntax}. 
 * However, to be able to use a Syntax it has to be registered via the 
 * {@link Syntax.Registry#register(SyntaxID, Syntax) Syntax.REGISTRY.register} method.
 * 
 * @author B. Ebertz
 */
public interface SyntaxID {
	/**
	 * @return a name identifying the syntax uniquely. 
	 */
	String getName();
}
