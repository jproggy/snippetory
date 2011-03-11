package de.jproggy.templa.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.jproggy.templa.spi.Syntax;

public class XMLAlikeSyntax extends RegExSyntax {

	@Override
	public RegexParser parse(CharSequence data) {
		Map<Pattern, TokenType> patterns = new HashMap<Pattern, Syntax.TokenType>();
		String chars = "\\p{Alnum}\\#\\=\\% ,:;._-";
		Pattern syntax = Pattern.compile("\\<s:([\\'" + chars + "]+)/\\>");
		patterns.put(syntax, TokenType.Syntax);

		Pattern start = Pattern.compile("\\<t:([\\'" + chars + "]+)\\>");
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile("^\\p{Space}\\<t:([\\'" + chars + "]+)\\>\\p{Space}(?:\\n\\r|\\n|\\r|\\r\\n)");
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile("\\</t:([\\p{Alnum}._-]+)\\>");
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile("^\\p{Space}\\</t:([\\p{Alnum}._-]+)\\>\\p{Space}(?:\\n\\r|\\n|\\r|\\r\\n)");
		patterns.put(end, TokenType.BlockEnd);

		Pattern field = Pattern.compile("\\{v:([\\'\\\"" + chars + "]+)\\}");
		patterns.put(field, TokenType.Field);
		return new RegexParser(data, patterns, chars);
	}
}
