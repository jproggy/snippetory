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

package org.jproggy.snippetory.engine.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.engine.Token.TokenType;

public class FluytSyntax extends RegExSyntax {
  protected static final String PLAIN_ATTRIBS = "\\s*" + ATTRIBUTE + "(?:\\s+" + ATTRIBUTE + ")*\\s*";
  protected static final String OPT_ATTRIBS = "(?:(?![" + NAME_CHAR + "\\(])|\\(" + PLAIN_ATTRIBS + "\\))";
  protected static final String MAND_ATTRIBS = "\\(" + PLAIN_ATTRIBS + "\\)";

  protected static final String START_TOKEN = "\\$((?:" + NAME + ")?" + OPT_ATTRIBS + ")\\{";
  protected static final String END_TOKEN = "\\}(" + NAME + ")?\\$";

  protected static final String NAMED_LOC = "\\$(" + NAME + OPT_ATTRIBS + ")(?:\\$)?";
  protected static final String NAMELESS_LOC = "\\$(" + MAND_ATTRIBS + ")(?:\\$)?";

  protected static final SyntaxVariant Block = SyntaxVariant.Block;
  protected static final SyntaxVariant Inline = SyntaxVariant.Inline;
  protected static final SyntaxVariant Named = SyntaxVariant.Named;
  protected static final SyntaxVariant Nameless = SyntaxVariant.Nameless;

  protected enum SyntaxVariant {
    Block, Inline, Named, Nameless
  }

  @Override
  public RegexParser parse(CharSequence data, TemplateContext ctx) {
    Map<Pattern, TokenType> patterns = createPatterns();
    return new RegexParser(data, ctx, patterns);
  }

  protected Map<Pattern, TokenType> createPatterns() {
    Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

    patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);

    createBlockPattern(patterns, TokenType.BlockStart, START_TOKEN);
    createInlinePattern(patterns, TokenType.BlockStart, START_TOKEN);

    createBlockPattern(patterns, TokenType.BlockEnd, END_TOKEN);
    createInlinePattern(patterns, TokenType.BlockEnd, END_TOKEN);

    createFieldPattern(patterns, Named, NAMED_LOC);
    createFieldPattern(patterns, Nameless, NAMELESS_LOC);

    Pattern comment = Pattern.compile(LINE_START + "///.*" + LINE_END, Pattern.MULTILINE);
    patterns.put(comment, TokenType.Comment);

    return patterns;
  }

  protected void createFieldPattern(Map<Pattern, TokenType> patterns, SyntaxVariant variant, String token) {
    Pattern field = Pattern.compile(coatStart(TokenType.Field, variant) + token + coatEnd(TokenType.Field, variant),
        Pattern.MULTILINE);
    patterns.put(field, TokenType.Field);
  }

  protected void createBlockPattern(Map<Pattern, TokenType> patterns, TokenType type, String token) {
    Pattern start = Pattern.compile(LINE_START + coatStart(type, Block) + token + coatEnd(type, Block) + LINE_END,
        Pattern.MULTILINE);
    patterns.put(start, type);
  }

  protected void createInlinePattern(Map<Pattern, TokenType> patterns, TokenType type, String token) {
    Pattern start = Pattern.compile(coatStart(type, Inline) + token + coatEnd(type, Inline), Pattern.MULTILINE);
    patterns.put(start, type);
  }

  protected String coatStart(TokenType type, SyntaxVariant variant) {
    return "";
  }

  protected String coatEnd(TokenType type, SyntaxVariant variant) {
    return "";
  }
}
