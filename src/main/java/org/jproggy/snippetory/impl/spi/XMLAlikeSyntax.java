package org.jproggy.snippetory.impl.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.impl.RegExSyntax;
import org.jproggy.snippetory.spi.Syntax;


public class XMLAlikeSyntax extends RegExSyntax {

	@Override
	public RegexParser parse(CharSequence data) {
		Map<Pattern, TokenType> patterns = new HashMap<Pattern, Syntax.TokenType>();
		String chars = "\\p{Alnum}\\#\\=\\% ,:;._-";
		String lEnd = "(?:\\n|\\r|\\r\\n|\\u0085|\\u2028|\\u2029)";
		
		Pattern syntax = Pattern.compile("^[ \\t]*\\<s:([" + chars + "]+)[ \\t]*/\\>[ \t]*" + lEnd, Pattern.MULTILINE);
		patterns.put(syntax, TokenType.Syntax);

		Pattern start = Pattern.compile("\\<t:([\\'\\\"" + chars + "]+)\\>");
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile("^[ \\t]*\\<t\\:([\\'\\\"" + chars + "]+)\\>[ \\t]*" + lEnd, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile("</t\\:([\\p{Alnum}._-]+)\\>");
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile("^[ \\t]*</t\\:([\\p{Alnum}._-]+)\\>[ \\t]*" + lEnd, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);

		Pattern field = Pattern.compile("\\{v:([\\'\\\"" + chars + "]+)\\}", Pattern.MULTILINE);
		patterns.put(field, TokenType.Field);
		return new RegexParser(data, patterns, chars);
	}
}
