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

import org.jproggy.snippetory.engine.FormatRegistry;

/**
 * The format allows to encapsulate and reuse portions of view logic in a simple and generic
 * way. Even though not each and every view logic can be packed into a format it provides a
 * significant support. <br />
 * Formats a created by {@link FormatFactory FormatFactories}, that parse the definition 
 * found within the template file in order to create an appropriate {@code Format}. 
 * 
 * @author B. Ebertz
 */
public interface Format {
	/**
	 * Register {@link FormatFactory FormatFactories} here.
	 */
	FormatRegistry REGISTRY = FormatRegistry.INSTANCE;

	/**
	 * may only be called for supported values.
	 */
	CharSequence format(Object value);
	
	/**
	 * Many formats only apply to a special type or even specific
	 * values. The format method will only be called for supported values
	 */
	boolean supports(Object value);
}
