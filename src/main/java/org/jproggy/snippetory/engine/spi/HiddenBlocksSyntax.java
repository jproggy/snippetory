package org.jproggy.snippetory.engine.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.spi.Syntax;


public class HiddenBlocksSyntax  extends RegExSyntax {

	@Override
	public RegexParser parse(CharSequence data) {
		Map<Pattern, TokenType> patterns = new HashMap<Pattern, Syntax.TokenType>();
		String chars = "\\p{Alnum}\\#\\=\\%\\\" ,:;._-";
		String pref = "(?:\\<\\!\\-\\-|\\/\\*)";
		String suff = "(?:\\-\\-\\>|\\*\\/)";
		String lEnd = "(?:\\n|\\r|\\r\\n|\\u0085|\\u2028|\\u2029)";
		Pattern syntax = Pattern.compile("^[ \\t]*" + pref + "s:([\\'" + chars + "]+)" + suff + "[ \\t]*" + lEnd, Pattern.MULTILINE);
		patterns.put(syntax, TokenType.Syntax);

		Pattern start = Pattern.compile(pref + "t\\:([\\'\\\"" + chars + "]+)" + suff);
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile("^[ \\t]*" + pref + "t\\:([\\'\\\"" + chars + "]+)" + suff + "[ \\t]*" + lEnd, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile(pref + "\\!t\\:([\\p{Alnum}._-]+)" + suff);
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile("^[ \\t]*" + pref + "\\!t\\:([\\p{Alnum}._-]+)" + suff + "[ \\t]*" + lEnd, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);

		Pattern field = Pattern.compile("\\{v:([\\'\\\"" + chars + "]+)\\}");
		patterns.put(field, TokenType.Field);
		return new RegexParser(data, patterns, chars);
	}
}
