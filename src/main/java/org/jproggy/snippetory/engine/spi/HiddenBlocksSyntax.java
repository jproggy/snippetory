package org.jproggy.snippetory.engine.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.spi.Syntax;


public class HiddenBlocksSyntax  extends RegExSyntax {

	@Override
	public RegexParser parse(CharSequence data) {
		Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, Syntax.TokenType>();

		String pref = "(?:\\<\\!\\-\\-|\\/\\*)";
		String suff = ")[ \\t]*(?:\\-\\-\\>|\\*\\/)";
		Pattern syntax = Pattern.compile(
				LINE_START + pref + "s:(" + NAME + suff + LINE_END, Pattern.MULTILINE);
		patterns.put(syntax, TokenType.Syntax);

		Pattern start = Pattern.compile(
				LINE_START + pref + "t\\:(" + NAME + ATTRIBUTES + suff + LINE_END, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile(
				pref + "t\\:(" + NAME + ATTRIBUTES + suff);
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile(
				LINE_START + pref + "\\!t\\:(" + NAME + suff + LINE_END, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile(pref + "\\!t\\:(" + NAME + suff);
		patterns.put(end, TokenType.BlockEnd);

		Pattern field = Pattern.compile("\\{v\\:(" + NAME + ATTRIBUTES + ")[ \\t]*\\}");
		patterns.put(field, TokenType.Field);
		return new RegexParser(data, patterns);
	}
}
