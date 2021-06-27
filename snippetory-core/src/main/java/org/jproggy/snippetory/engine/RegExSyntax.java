/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

package org.jproggy.snippetory.engine;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Token.TokenType;
import org.jproggy.snippetory.spi.Syntax;

public abstract class RegExSyntax implements Syntax {
  protected static final String LINE_END = "[ \\t]*(?>(?>\\r\\n?)|\\n|\\u0085|\\u2028|\\u2029|\\Z)";
  protected static final String LINE_START = "^[ \\t]*";

  protected static final String NAME_START_CHAR = "[\\p{javaJavaIdentifierStart}&&[^$]]";
  protected static final String NAME_CHAR = "[\\p{javaJavaIdentifierPart}.\\-&&[^$]]";
  protected static final String NAME = NAME_START_CHAR + NAME_CHAR + "*";

  protected static final String ESCAPES = "\\\\\\\\|\\\\'|\\\\\"|\\\\n|\\\\r|\\\\b|\\\\t|\\\\f";
  private static final String QUOTE_VALUE = "\\\"(?:" + ESCAPES + "|[^\\\\\"])*\\\"";
  private static final String APOS_VALUE = "\\'(?:" + ESCAPES + "|[^\\\\'])*\\'";
  protected static final String ATTRIBUTE = NAME + "=(?:" + APOS_VALUE + "|" + QUOTE_VALUE + ")";
  protected static final String ATTRIBUTES = "(?:\\s+" + ATTRIBUTE + ")*";

  private static final String QUOTE_CONTENT = "\"((?>" + ESCAPES + "|[^\\\\\"])*)\")";
  private static final String APOS_CONTENT = "'((?>" + ESCAPES + "|[^'])*)'";
  private static final String CONTENT = "(" + NAME + ")=(?>" + APOS_CONTENT + "|" + QUOTE_CONTENT + "|(" + NAME + ")";

  private static final String REM_START = "(?://|/\\*|<!--|--|#|'|rem)";
  protected static final Pattern SYNTAX_SELECTOR = Pattern.compile(LINE_START + "(?:" + REM_START
          + "[ \\t]*Syntax|<s):(" + NAME + ")(?:\\*/|-|/|>| |\\t)*" + LINE_END, Pattern.MULTILINE);

  @Override
  public abstract RegexParser parse(CharSequence data, TemplateContext ctx);

  @Override
  public Tokenizer takeOver(Tokenizer data) {
    RegexParser p = parse(data.getData(), data.getContext());
    p.jumpTo(data.getPosition());
    return p;
  }

  protected static class RegexParser implements Syntax.Tokenizer {
    private final Map<Pattern, TokenType> patterns;
    private final Matcher matcher;
    private final CharSequence data;
    private Boolean found;
    private int pos = 0;
    private final TemplateContext context;

    public RegexParser(CharSequence data, TemplateContext ctx, Map<Pattern, TokenType> patterns) {
      this.patterns = patterns;
      String compoundPattern = patterns.keySet().stream()
              .map(p -> "(?:" + p.pattern() + ')')
              .collect(Collectors.joining("|"));
      matcher = Pattern.compile(compoundPattern, Pattern.MULTILINE).matcher(data);
      this.data = data;
      this.context = ctx;
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
      if (Boolean.FALSE.equals(found)) {
        return part(matcher.regionEnd());
      }
      if (pos < matcher.start()) {
        return part(matcher.start());
      }
      String content = getContent();
      pos = matcher.end();
      TokenType type = analyze(matcher.group());
      if (type == TokenType.Comment) {
        return new Token(null, matcher.group(), type, matcher.start(), this);
      }
      if (type == TokenType.BlockEnd) {
        return new Token(content, matcher.group(), type, matcher.start(), this);
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
      Token t = new Token(null, content, TokenType.TemplateData, pos, this);
      pos = endPos;
      return t;
    }

    private static final Pattern VARI = Pattern.compile(CONTENT);

    protected Token createToken(String varDef, TokenType type) {
      Matcher m = VARI.matcher(varDef);
      Token token = null;
      while (m.find()) {
        if (token == null) {
          token = new Token(m.group(4), matcher.group(), type, matcher.start(), this);
          if (m.group(4) != null) continue;
        }
        if (m.group(4) != null) throw new ParseError("don't understand " + varDef, token);
        if (AttributesRegistry.INSTANCE.type(m.group(1)) == null) {
          throw new ParseError("unknown attribute name " + m.group(1), token);
        }
        String value = m.group(2);
        if (value == null) value = m.group(3);
        value = decode(value, token);
        token.getAttributes().put(m.group(1), value);
      }
      if (token == null) {
        // no name, no attributes
        return new Token(null, matcher.group(), type, matcher.start(), this);
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
          } else {
            result.append(val.charAt(i));
          }
        }
      }
      return result.toString();
    }

    public String getContent() {
      for (int i = 1; i <= matcher.groupCount(); i++) {
        if (matcher.group(i) != null) return matcher.group(i);
      }
      return null;
    }

    private TokenType analyze(String element) {
      for (Map.Entry<Pattern, TokenType> e : patterns.entrySet()) {
        if (e.getKey().matcher(element).matches()) return e.getValue();
      }
      return null;
    }

    @Override
    public TemplateContext getContext() {
      return context;
    }

    private static final Pattern LINES = Pattern.compile("\\r\\n?|\\n|\\u0085|\\u2028|\\u2029", Pattern.MULTILINE);

    @Override
    public TextPosition getPosition(Token t) {
      Matcher lineCutter = LINES.matcher(data);
      int lineStart = 1;
      int line = 1;
      while (lineCutter.find() && lineCutter.end() <= t.getPosition()) {
        lineStart = lineCutter.end();
        line++;
      }
      return new TextPosition(line, (t.getPosition() - lineStart) + 1);
    }
  }
}
