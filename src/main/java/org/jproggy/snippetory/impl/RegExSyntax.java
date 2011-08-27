package org.jproggy.snippetory.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jproggy.snippetory.spi.Syntax;


public abstract class RegExSyntax implements Syntax {

	@Override
	public abstract RegexParser parse(CharSequence data) ;

	@Override
	public Tokenizer takeOver(Tokenizer data) {
		RegexParser p = parse(data.getData());
		p.jumpTo(data.getPosition());
		return p;
	}

	protected static class RegexParser implements Syntax.Tokenizer {
		private final Map<Pattern, TokenType> patterns; 
		private final Matcher matcher;
		private final CharSequence data;
		private final String chars;
		private Boolean found;
		private int pos = 0;

		public RegexParser(CharSequence data, Map<Pattern, TokenType> patterns, String chars) {
			this.patterns = patterns;
			String compoundPattern = "";
			for (Pattern p : patterns.keySet()) {
				if (compoundPattern.length() > 0 ) compoundPattern += "|";
				compoundPattern += "(?:" + p.pattern() + ')';
			}
			matcher = Pattern.compile(compoundPattern, Pattern.MULTILINE).matcher(data);
			this.data = data;
			this.chars = chars;
		}

		@Override
		public boolean hasNext() {
			return pos < matcher.regionEnd();
		}

		@Override
		public CharSequence getData() {
			return data;
		}

		@Override
		public int getPosition() {
			return pos;
		}

		@Override
		public Token next() {
			if (found == null || pos == matcher.end()) {
				found = matcher.find();
			}
			if (found == Boolean.FALSE) {
				return part(matcher.regionEnd());
			}
			if (pos < matcher.start()) {
				return part(matcher.start());
			}
			String content = getContent();
			pos = matcher.end();
			TokenType type = analyze(matcher.group());
			if (type == TokenType.BlockEnd) {
				return new Token(content, matcher.group(), type, matcher.start());
			}
			return createToken(content, type);
		}
		
		@Override
		public void jumpTo(int position) {
			matcher.region(position, matcher.regionEnd());
			pos = position;
		}

		private Token part(int endPos) {
			String content = data.subSequence(pos, endPos).toString();
			Token t = new Token(null, content, TokenType.TemplateData, pos);
			pos = endPos;
			return t;
		}

		protected Token createToken(String varDef, TokenType type) {
			Pattern vari = Pattern.compile("([\\p{Alnum}._-]+)|(?: ([\\p{Alnum}_]+)=(?:'([\\\"" + chars + "]+)'|\\\"([\\'" + chars + "]+)\\\"))");
			Matcher m = vari.matcher(varDef);
			m.find();
			Token token = new Token(m.group(), matcher.group(), type,
					matcher.start());
			while (m.find()) {
				if (m.group(1) != null)
					throw new IllegalArgumentException("don't understand "
							+ varDef);
				if (Attributes.REGISTRY.type(m.group(2)) == null) {
					throw new IllegalArgumentException("unkown attribute name "
							+ m.group(2));
				}
				String value = m.group(3);
				if (value == null) value = m.group(4);
				token.getAttributes().put(m.group(2), value);
			}
			return token;
		}

		public String getContent() {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				if (matcher.group(i) != null)
					return matcher.group(i);
			}
			return null;
		}

		private TokenType analyze(String element) {
			for (Map.Entry<Pattern, TokenType> e: patterns.entrySet()) {
				if (e.getKey().matcher(element).matches()) return e.getValue();
			}
			return null;
		}
	}
}
