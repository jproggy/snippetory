package org.jproggy.snippetory.engine.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.engine.Token.TokenType;

public class CComments   extends RegExSyntax {

	@Override
	public RegexParser parse(CharSequence data) {
		Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

		patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);
		String cbs = "\\/\\*[ \t]*";
		String cbe = "[ \\t]*\\*\\/";

		String pre = cbs + "\\$\\{[ \t]*";
		String suff = "[ \\t]*\\}" + cbe;

		Pattern mock_default = Pattern.compile(
				pre +"(" + NAME + "(?:[ \t]*" + ATTRIBUTE + ")*)" + cbe + "(.*)" + cbs + suff);
		patterns.put(mock_default, TokenType.Field);

		Pattern start = Pattern.compile(
				LINE_START +  "\\/\\/[ \t]*\\$\\{[ \t]*(" + NAME + 
				"(?:[ \t]*" + ATTRIBUTE + ")*)" + LINE_END, Pattern.MULTILINE);
		patterns.put(start, TokenType.BlockStart);

		start = Pattern.compile(
				pre + "(" + NAME + "(?:[ \t]*" + ATTRIBUTE + ")*)" + cbe);
		patterns.put(start, TokenType.BlockStart);

		Pattern end = Pattern.compile(
				LINE_START + "\\/\\/[ \t]*(" + NAME + ")[ \t]*\\}" + LINE_END, Pattern.MULTILINE);
		patterns.put(end, TokenType.BlockEnd);

		end = Pattern.compile(cbs + "(" + NAME + ")[ \t]*\\}" + cbe);
		patterns.put(end, TokenType.BlockEnd);
		
		Pattern comment = Pattern.compile(
				LINE_START + "\\/\\/\\/.*" +  LINE_END, Pattern.MULTILINE);
		patterns.put(comment, TokenType.Comment);
		
		Pattern field = Pattern.compile(pre + "(" + NAME + "(?:[ \t]*" + ATTRIBUTE + ")*)" + suff);
		patterns.put(field, TokenType.Field);

		Pattern nameless = Pattern.compile(pre + "(" + ATTRIBUTE + "(?:[ \t]*" + ATTRIBUTE + ")*)" + suff);
		patterns.put(nameless, TokenType.Field);
		return new RegexParser(data, patterns);
	}
}
