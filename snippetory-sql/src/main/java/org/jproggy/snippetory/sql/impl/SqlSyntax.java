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

package org.jproggy.snippetory.sql.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.Token.TokenType;
import org.jproggy.snippetory.engine.spi.FluytSyntax;
import org.jproggy.snippetory.spi.SyntaxID;

public class SqlSyntax extends FluytSyntax implements SyntaxID {
  @Override
  protected Map<Pattern, TokenType> createPatterns() {
    Map<Pattern, TokenType> patterns = new LinkedHashMap<>();

    patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);

    createBlockPattern(patterns, TokenType.BlockStart, START_TOKEN);
    createInlinePattern(patterns, TokenType.BlockStart, START_TOKEN);

    createBlockPattern(patterns, TokenType.BlockEnd, END_TOKEN);
    createInlinePattern(patterns, TokenType.BlockEnd, END_TOKEN);

    String field = "[:$](" + NAME + "/\\*(?:" + PLAIN_ATTRIBS + ")?)\\*/";
    createFieldPattern(patterns, SyntaxVariant.Named, field);

    field = "[:$](" + NAME + ")" ;
    createFieldPattern(patterns, SyntaxVariant.Named, field);

    field = "/\\*\\$(" + NAME + ")\\*/" ;
    createFieldPattern(patterns, SyntaxVariant.Named, field);

    Pattern comment = Pattern.compile(LINE_START + "///.*" + LINE_END, Pattern.MULTILINE);
    patterns.put(comment, TokenType.Comment);

    return patterns;
  }

  @Override
  protected String coatStart(TokenType type, SyntaxVariant variant) {
    if (variant == SyntaxVariant.Named) return "";
    return "(?:(?:/\\*|--)[ \t]*)?";
  }

  @Override
  protected String coatEnd(TokenType type, SyntaxVariant variant) {
    if (variant == SyntaxVariant.Named) return "";
    return "(?:[ \t]*(?:\\*/))?";
  }

  @Override
  public String getName() {
    return "sql";
  }
}
