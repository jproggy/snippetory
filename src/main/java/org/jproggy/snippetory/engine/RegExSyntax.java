package org.jproggy.snippetory.engine;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jproggy.snippetory.spi.Syntax;


public abstract class RegExSyntax implements Syntax {
	protected static final String LINE_END = 
		"[ \t]*(?:\\n|\\r|\\r\\n|\\u0085|\\u2028|\\u2029)";
	protected static final String LINE_START = "^[ \\t]*";
	protected final static String ESCAPES = "\\\\\\\\|\\\\'|\\\\\"|\\n|\\r|\\b|\\t|\\f";
	protected static final String NAME = "[\\p{Alnum}\\#\\._-]+";
	protected static final String ATTRIBUTES = 
		"(?:[ \t]+" + NAME + "=(?:\\'(?:" + ESCAPES + "|[^\\\\'])*\\'|\\\"(?:" + ESCAPES + 
		"|[^\\\\\"])*\\\"))*";
	protected static final String CONTENT = 
		"(" + NAME + ")|[ \\t]+(" + NAME + ")=(?:\\'((?:" + ESCAPES + 
		"|[^\\'])*)\\'|\\\"((?:" + ESCAPES + "|[^\"])*)\\\")"; 

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
		private Boolean found;
		private int pos = 0;

		public RegexParser(CharSequence data, Map<Pattern, TokenType> patterns) {
			this.patterns = patterns;
			String compoundPattern = "";
			for (Pattern p : patterns.keySet()) {
				if (compoundPattern.length() > 0 ) compoundPattern += "|";
				compoundPattern += "(?:" + p.pattern() + ')';
			}
			matcher = Pattern.compile(compoundPattern, Pattern.MULTILINE).matcher(data);
			this.data = data;
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

		private static final Pattern vari = Pattern.compile(CONTENT);
		protected Token createToken(String varDef, TokenType type) {
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
				value = decode(value, token);
				token.getAttributes().put(m.group(2), value);
			}
			return token;
		}
		
		private String decode(String val, Token t) {
			StringBuilder result = new StringBuilder();
			boolean bsFound = false;
			for (int i = 0; i < val.length(); i++) {
				if (bsFound) {
					switch (val.charAt(i)) {
					case '\\':
						result.append('\\');
						break;
					case 'n':
						result.append('\n');
						break;
					case 'r':
						result.append('\r');
						break;
					case 't':
						result.append('\t');
						break;
					case 'b':
						result.append('\b');
						break;
					case 'f':
						result.append('\f');
						break;
					case '\'':
						result.append('\'');
						break;
					case '"':
						result.append('"');
						break;

					default:
						throw new ParseError("Unkown escaped character. " + val.charAt(i), t);
					}
					bsFound = false;
				} else {
					if (val.charAt(i) == '\\') {
						bsFound = true;
					} else result.append(val.charAt(i));
				}
			}
			return result.toString();
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
