package org.jproggy.snippetory.engine.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.engine.Token.TokenType;


public class XMLAlikeSyntax extends RegExSyntax {
	@Override
	public RegexParser parse(CharSequence data) {
		Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();
		
		patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);

		Pattern start = Pattern.compile(
				LINE_START + "\\<t\\:(" + NAME + "(?:\\s+" + ATTRIBUTE + ")*)\\s*\\>" + LINE_END, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile("\\<t:(" + NAME + "(?:\\s+" + ATTRIBUTE + ")*)\\s*\\>");
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile(
				LINE_START + "</t\\:(" + NAME + ")\\>" + LINE_END, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile("</t\\:(" + NAME + ")\\>");
		patterns.put(end, TokenType.BlockEnd);

		Pattern field = Pattern.compile(
				"\\{v\\:(" + NAME + "(?:\\s+" + ATTRIBUTE + ")*)[ \\t]*\\}", Pattern.MULTILINE);
		patterns.put(field, TokenType.Field);

		Pattern nameless = Pattern.compile(
				"\\{v\\:\\s*(" + ATTRIBUTE + "(?:\\s+" + ATTRIBUTE + ")*)\\s*\\}", Pattern.MULTILINE);
		patterns.put(nameless, TokenType.Field);
		return new RegexParser(data, patterns);
	}
}
