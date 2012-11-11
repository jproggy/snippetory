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

import java.io.IOException;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.EncodingRegistry;
import org.jproggy.snippetory.engine.IncompatibleEncodingException;

/**
 * The purpose of an encoding is to ensure the syntactical correctness of an output by 
 * escaping terms or characters with special meaning in the syntax of the output file.
 * For example the ampersand is illegal within xml as it's used to mark an entity.
 * It has to be replaced by &amp;amp;.<br />
 * By handling those technical issues of the output file within the template definition
 * the handling logic gets more reusable. And simpler to implement. This is a simple but
 * very efficient abstraction layer.<br />
 * As the encoding is inherited throughout the tree of snippets within a {@link Template} 
 * a single encoding definition is sufficient for many cases. However, combination of encodings 
 * is common as well. This is why it can be overwritten as often as needed, for entire subtrees
 * or just for single leaf nodes. 
 */
public interface Encoding {
	/**
	 * Register Encodings here.
	 */
	EncodingRegistry REGISTRY = EncodingRegistry.INSTANCE;
	
	/**
	 * Sometimes it's possible to combine data encoded in different ways after applying 
	 * a special action to one of the strings. This action might be a translation like 
	 * wiki syntax to html or simply apply default escaping to the data and mix encodings
	 * that way. I.e. when adding html to string-encoded data this is possible. However, 
	 * line breaks or quotation marks will have to be escaped. (We are talking about a
	 * file that contains a definition of a string of course) <br />
	 * In other cases no action will be needed. String encoded data can be added to html
	 * as this is a container format and is able to carry string definition within script-
	 * section for instance. <br />
	 * However, some combinations of encodings are illegal. Especially the plain encoding
	 * can't be combined with others. In those cases an {@link IncompatibleEncodingException}
	 * is thrown.
	 * 
	 * @param target result of the action has to be appended to target.
	 * @param value has be transcoded
	 * @param encodingName Value is already encoded with this encoding
	 * @throws IncompatibleEncodingException if the encoding can't be taken as is and can't
	 * be decoded.
	 */
	void transcode(Appendable target, CharSequence value, String encodingName) throws IOException, IncompatibleEncodingException;
	
	/**
	 * The identifier for registering and retrieval of this Encoding  
	 */
	String getName();
}
