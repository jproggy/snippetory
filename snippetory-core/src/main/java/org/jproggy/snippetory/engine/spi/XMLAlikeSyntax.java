/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

package org.jproggy.snippetory.engine.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.engine.Token.TokenType;

public class XMLAlikeSyntax extends RegExSyntax {
	protected static final String START_TOKEN = "\\<t\\:((?:" + NAME + ")?" + ATTRIBUTES + ")\\s*\\>";
	protected static final String END_TOKEN = "</t\\:(" + NAME + ")?\\>";

	protected static final String NAMED_LOC = "\\{v\\:(" + NAME + ATTRIBUTES + ")[ \\t]*\\}";
	protected static final String NAMELESS_TOKEN = "\\{v\\:\\s*(" + ATTRIBUTE + ATTRIBUTES + ")\\s*\\}";

	@Override
	public RegexParser parse(CharSequence data, TemplateContext ctx) {
		Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

		patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);

		addRegionPatterns(patterns);

		Pattern field = Pattern.compile(NAMED_LOC, Pattern.MULTILINE);
		patterns.put(field, TokenType.Field);

		Pattern nameless = Pattern.compile(NAMELESS_TOKEN, Pattern.MULTILINE);
		patterns.put(nameless, TokenType.Field);
		return new RegexParser(data, ctx, patterns);
	}

	protected static void addRegionPatterns(Map<Pattern, TokenType> patterns) {
		Pattern start = Pattern.compile(LINE_START + START_TOKEN + LINE_END, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile(START_TOKEN, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile(LINE_START + END_TOKEN + LINE_END, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile(END_TOKEN, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);
	}
}
