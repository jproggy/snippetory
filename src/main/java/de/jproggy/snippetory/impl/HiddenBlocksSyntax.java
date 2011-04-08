package de.jproggy.snippetory.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.jproggy.snippetory.spi.Syntax;

public class HiddenBlocksSyntax  extends RegExSyntax {

	@Override
	public RegexParser parse(CharSequence data) {
		Map<Pattern, TokenType> patterns = new HashMap<Pattern, Syntax.TokenType>();
		String chars = "\\p{Alnum}\\#\\=\\%\\\" ,:;._-";
		String pref = "(?:\\<\\!\\-\\-|\\/\\*)";
		String suff = "(?:\\-\\-\\>|\\*\\/)";
		Pattern syntax = Pattern.compile(pref + "s:([\\'" + chars + "]+)" + suff);
		patterns.put(syntax, TokenType.Syntax);

		Pattern start = Pattern.compile(pref + "t:([\\'" + chars + "]+)" + suff);
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile("^\\p{Space}" + pref + "t:([" + chars + "]+)" + suff + "\\p{Space}(?:\\n\\r|\\n|\\r|\\r\\n)");
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile(pref + "!t:([\\p{Alnum}._-]+)" + suff);
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile("^\\p{Space}" + pref + "!t:([\\p{Alnum}._-]+)" + suff + "\\p{Space}(?:\\n\\r|\\n|\\r|\\r\\n)");
		patterns.put(end, TokenType.BlockEnd);

		Pattern field = Pattern.compile("\\{v:([\\'" + chars + "]+)\\}");
		patterns.put(field, TokenType.Field);
		return new RegexParser(data, patterns, chars);
	}
}
