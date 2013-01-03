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
 * way.
 * <h2>Format definition in template file</h2> 
 * Formats are activated by a single attribute, that's evaluated by a {@link FormatFactory}, 
 * that parses this definition, found within the template file, in order to create an appropriate 
 * {@code Format}. The format itself may be configured by additional sub-attributes. <br />
 * Sub attributes have the form {@code<main-attribute>.<sub-attribute>} and directly follow the
 * main attribute. For instance: 
 * <p>
 * {@code stretch="20" stretch.align="left"}
 * </p>
 * Sub-attributes are implemented as bean properties of the {@code Format} implementation:
 * <p>
 * <code>
 *  enum Alignment {left, right};<br />
 *  public void setAlign(Alignment value) {..
 * </code>
 * </p>
 * This mechanism provides support for numbers, boolean, enums, {@link Encoding Encodings} and strings. 
 * Alternatively, especially when the attributes are not known at build time, this can be done
 * by implementing {@link DynamicAttributes} instead.
 * <h2>Binding data to formats at runtime</h2>
 * 
 * Even though not each and every view logic can be packed into a format it provides a
 * significant support.
 * 
 * @see <a href="http://www.jproggy.org/snippetory/Formats.html">Official documentation on formats</a>
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
	Object format(TemplateNode location, Object value);
	
	/**
	 * Many formats only apply to a special type or even specific
	 * values. The format method will only be called for supported values
	 */
	boolean supports(Object value);
	
	void clear(TemplateNode location);
}
