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

import static org.jproggy.snippetory.engine.spi.FluytSyntax.SyntaxVariant.Block;
import static org.jproggy.snippetory.engine.spi.FluytSyntax.SyntaxVariant.Inline;
import static org.jproggy.snippetory.engine.spi.FluytSyntax.SyntaxVariant.Named;
import static org.jproggy.snippetory.engine.spi.FluytSyntax.SyntaxVariant.Nameless;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.engine.Token.TokenType;

public class FluytSyntax extends RegExSyntax {
	protected static final String PLAIN_ATTRIBS = "\\s*" + ATTRIBUTE + "(?:\\s+" + ATTRIBUTE + ")*\\s*";
	protected static final String OPT_ATTRIBS = "(?:(?![" + NAME_CHAR + "\\(])|\\(" + PLAIN_ATTRIBS + "\\)|\\(\\))";
	protected static final String MAND_ATTRIBS = "\\(" + PLAIN_ATTRIBS + "\\)";

	protected static final String START_TOKEN = "\\#((?:" + NAME + ")?" + OPT_ATTRIBS + ")\\{";
	protected static final String END_TOKEN = "\\}(" + NAME + ")?\\#";

	protected static final String NAMED_LOC = "\\#(" + NAME + OPT_ATTRIBS + ")";
	protected static final String NAMELESS_LOC = "\\#(" + MAND_ATTRIBS + ")";

	protected enum SyntaxVariant {
		Block, Inline, Named, Nameless
	}

	@Override
	public RegexParser parse(CharSequence data, TemplateContext ctx) {
		Map<Pattern, TokenType> patterns = createPatterns();
		return new RegexParser(data, ctx, patterns);
	}

	protected Map<Pattern, TokenType> createPatterns() {
		Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

		patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);

		createBlockPattern(patterns, TokenType.BlockStart, START_TOKEN);
		createInlinePattern(patterns, TokenType.BlockStart, START_TOKEN);

		createBlockPattern(patterns, TokenType.BlockEnd, END_TOKEN);
		createInlinePattern(patterns, TokenType.BlockEnd, END_TOKEN);

		createFieldPattern(patterns, Named, NAMED_LOC);
		createFieldPattern(patterns, Nameless, NAMELESS_LOC);

		Pattern comment = Pattern.compile(LINE_START + "///.*" + LINE_END, Pattern.MULTILINE);
		patterns.put(comment, TokenType.Comment);

		return patterns;
	}

	protected void createFieldPattern(Map<Pattern, TokenType> patterns, SyntaxVariant variant, String token) {
		Pattern field = Pattern.compile(
				coatStart(TokenType.Field, variant) + token + coatEnd(TokenType.Field, variant), Pattern.MULTILINE);
		patterns.put(field, TokenType.Field);
	}

	protected void createBlockPattern(Map<Pattern, TokenType> patterns, TokenType type, String token) {
		Pattern start = Pattern.compile(LINE_START + coatStart(type, Block) + token + coatEnd(type, Block) + LINE_END,
				Pattern.MULTILINE);
		patterns.put(start, type);
	}

	protected void createInlinePattern(Map<Pattern, TokenType> patterns, TokenType type, String token) {
		Pattern start = Pattern.compile(coatStart(type, Inline) + token + coatEnd(type, Inline), Pattern.MULTILINE);
		patterns.put(start, type);
	}

	protected String coatStart(TokenType type, SyntaxVariant variant) {
		return "";
	}

	protected String coatEnd(TokenType type, SyntaxVariant variant) {
		return "";
	}
}
