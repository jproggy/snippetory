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

package org.jproggy.snippetory.engine.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.engine.Token.TokenType;

/**
 * C_COMMENTS syntax uses C comment areas and C++ line comments to hide template syntax 
 * from compilers and interpreters that might be used to validate the templates.
 * <br />
 * The template syntax is based on a leading $ sign and braces to mark the area 
 * of impact. On regions the name is repeated at the end, while pure locations
 * simply close the curly bracket.
 * <br />
 * A special variant of the syntax allows mocking:
 * <br />
 * <code style="color:darkblue;">
 * /*${name attrib="value"&#42;/<b>mock</b>/*}&#42;/;
 * </code>
 * <br /> 
 * The mock will be ignored and will not be written to the output. This supports
 * to keep the template valid for execution or compilation as for the validation
 * environment the mock is visible and substitutes data to be bound.
 * <br />
 * Line breaks are supported whitespace in C_COMMENTS but not within the mock.
 * 
 * @author B. Ebertz
 */
public class CComments   extends RegExSyntax {

	@Override
	public RegexParser parse(CharSequence data, TemplateContext ctx) {
		Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

		patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);
		String cbs = "\\/\\*\\s*";
		String cbe = "\\s*\\*\\/";

		String pre = cbs + "\\$\\{\\s*";
		String suff = "\\s*\\}" + cbe;

		Pattern mock_default = Pattern.compile(
				pre +"(" + NAME + "(?:\\s+" + ATTRIBUTE + ")*)" + cbe + "((?:(?!/\\*).)*)" + cbs + suff, Pattern.MULTILINE);
		patterns.put(mock_default, TokenType.Field);

		Pattern start = Pattern.compile(
				LINE_START +  "\\/\\/\\s*\\$\\{[\\s/]*(" + NAME + 
				"(?:\\s[\\s/]*" + ATTRIBUTE + ")*)" + LINE_END, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile(
				pre + "(" + NAME + "(?:\\s+" + ATTRIBUTE + ")*)" + cbe, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile(
				LINE_START + "\\/\\/\\s*(" + NAME + ")\\s*\\}" + LINE_END, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile(cbs + "(" + NAME + ")\\s*\\}" + cbe, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);
		
		Pattern comment = Pattern.compile(
				LINE_START + "\\/\\/\\/.*" +  LINE_END, Pattern.MULTILINE);
		patterns.put(comment, TokenType.Comment);
		
		Pattern field = Pattern.compile(pre + "(" + NAME + "(?:\\s+" + ATTRIBUTE + ")*)" + suff, Pattern.MULTILINE);
		patterns.put(field, TokenType.Field);

		Pattern nameless = Pattern.compile(pre + "(" + ATTRIBUTE + "(?:\\s+" + ATTRIBUTE + ")*)" + suff, Pattern.MULTILINE);
		patterns.put(nameless, TokenType.Field);
		return new RegexParser(data, ctx, patterns);
	}
}
