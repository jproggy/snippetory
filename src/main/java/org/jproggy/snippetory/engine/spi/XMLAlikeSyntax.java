package org.jproggy.snippetory.engine.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.spi.Syntax;


public class XMLAlikeSyntax extends RegExSyntax {
	@Override
	public RegexParser parse(CharSequence data) {
		Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, Syntax.TokenType>();
		
		Pattern syntax = Pattern.compile("(?://|<!|--| |\\t)*\\<s:(" + NAME + ")[ \\t]*/\\>(?:-->| |\\t)*" + LINE_END, Pattern.MULTILINE);
		patterns.put(syntax, TokenType.Syntax);

		Pattern start = Pattern.compile(LINE_START + "\\<t\\:(" + NAME + ATTRIBUTES + ")[ \\t]*\\>" + LINE_END, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile("\\<t:(" + NAME + ATTRIBUTES + ")[ \\t]*\\>");
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile(LINE_START + "</t\\:(" + NAME + ")\\>" + LINE_END, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile("</t\\:(" + NAME + ")\\>");
		patterns.put(end, TokenType.BlockEnd);

		Pattern field = Pattern.compile("\\{v\\:(" + NAME + ATTRIBUTES + ")[ \\t]*\\}", Pattern.MULTILINE);
		patterns.put(field, TokenType.Field);
		return new RegexParser(data, patterns);
	}
}
