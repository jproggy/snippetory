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

import java.util.Set;

import org.jproggy.snippetory.engine.spi.DefaultFormatter;
import org.jproggy.snippetory.engine.spi.ToggleFormatter;

/**
 * <p>
 * Extends the format to support the special case, that no value has been
 * provided to a location via {@link Template#set} or {@link Template#append}
 * method. (When rendering to a template append will be used internally.) Just
 * like the default format does. Other implementations can provide more
 * sophisticated algorithms to evaluate the rendered value.
 * </p>
 * <p>
 * VoidFormats are often used 
 * </p>
 * <p>
 * <b>Be aware that only the first VoidFormat per location will be executed.</b>
 * </p>
 * <p>
 * <b>Be aware that VoidFormats are not inherited through the template tree.</b>
 * </p>
 * 
 * @author B. Ebertz
 * 
 * @see Format
 * @see DefaultFormatter
 * @see ToggleFormatter
 * 
 */
public interface VoidFormat extends Format {
	
	/**
	 * Provide a representation   
	 * @param node
	 * @return
	 */
	Object formatVoid(TemplateNode node);

	/**
	 * <p>
	 * Offers a value to the callee. The callee is responsible for filtering out
	 * the relevant names. Thus it's expected to ignore all names, that aren't
	 * listed in it's names(). However, the caller may do this filtering, too,
	 * as far as it provides a names declared by the names() method. The set
	 * method is intended to keep only a single value.
	 * </p>
	 */
	void set(String name, Object value);

	/**
	 * Appends a String representation of the value to all variables with given
	 * name. The exact value might differ according to different meta data
	 * associated with each of these variables. Eventually set or appended data
	 * is kept and new data is appended behind the last character. All matching
	 * formats and encodings are used. However, there is some special handling
	 * for the interface (@link EncodedData). In this case the provided encoding
	 * in determined to calculate the correct transcoding.
	 */
	void append(String name, Object value);

	/**
	 * Declares the names supported by this Format. The returned Set must not
	 * change over time. It may return different instances, but it must be
	 * reliable to cache the result.
	 */
	Set<String> names();
}
