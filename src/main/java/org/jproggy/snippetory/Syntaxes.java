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

package org.jproggy.snippetory;

import org.jproggy.snippetory.spi.SyntaxID;

/**
 * Provides direct access to the predefined syntaxes. 
 * For more information see 
 * <a href="http://www.jproggy.org/snippetory/Syntax.html">
 * syntaxes in official documentation
 * </a>.
 * @author B.Ebertz
 */
public enum Syntaxes implements SyntaxID {
	/**
	 * This is the default syntax. It is used if no syntax is defined. It
	 * looks like this: 
	 * <p>
	 * &lt;t:name default='def'>{v:other_name}&lt;/t:name>
	 * </p>
	 * It integrates fine in xml-based formats and is very visible in many others.
	 * It provides low risk of incompatibility to a certain output format.
	 */
	XML_ALIKE,
	C_COMMENTS,
	FLUYT,
	
	/**
	 * This syntax allows template markup, that is invisible to the parser
	 * of many output formats. There are variants based on &lt;!-- --> and on
	 * &#47;* *&#47; that can be mixed freely as needed:
	 * <p>
	 * &#47;*t:name1-->{v:name2}&lt;!--t:name3 default='def'-->&#47;*!t:name3*&#47;
	 * <--!t:name1*&#47;
	 * </p>  
	 */
	HIDDEN_BLOCKS;
	
	@Override
    public String getName() { return name(); }
	
	/**
	 *  a template context pre-configured for selected syntax 
	 */
	public TemplateContext context() {
		return new TemplateContext().syntax(this);
	}
	
	/**
	 * parse with selected syntax
	 * @param data input data to parse
	 * @return the template for the parsed data
	 */
	public Template parse(CharSequence data) {
		return context().parse(data);
	}
}