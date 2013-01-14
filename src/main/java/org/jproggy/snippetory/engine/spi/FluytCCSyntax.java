package org.jproggy.snippetory.engine.spi;

import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.Token.TokenType;

public class FluytCCSyntax extends FluytSyntax {

	@Override
	protected Map<Pattern, TokenType> createPatterns() {
		Map<Pattern, TokenType> patterns = super.createPatterns();
		String mock = "[ \t]*\\*/((?:(?!/\\*).)*)/\\*[ \t]*";
		
		String field = "\\$(" + NAME + "\\((?:" + PLAIN_ATTRIBS + ")?" + mock + "\\))";
		createFieldPattern(patterns, SyntaxVariant.Named, field);
		
		field = "\\$(\\(" + PLAIN_ATTRIBS + mock + "\\))";
		createFieldPattern(patterns, SyntaxVariant.Nameless, field);

		return patterns;
	}

	@Override
	protected String coatStart(TokenType type, SyntaxVariant variant) {
		return "(?:(?:/\\*|//)[ \t]*)?";
	}

	@Override
	protected String coatEnd(TokenType type, SyntaxVariant variant) {
		return "(?:[ \t]*(?:\\*/))?";
	}
}
