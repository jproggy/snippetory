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

import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.spi.FluytSyntax;
import org.jproggy.snippetory.spi.SyntaxID;
import org.jproggy.snippetory.util.Token.TokenType;

public class SqlSyntax extends FluytSyntax implements SyntaxID {
  @Override
  protected Map<Pattern, TokenType> createPatterns() {
    Map<Pattern, TokenType> patterns = super.createPatterns();

    String field = ":(" + NAME + "/\\*(?:" + PLAIN_ATTRIBS + ")?)\\*/";
    createFieldPattern(patterns, SyntaxVariant.Named, field);

    field = ":(" + NAME + ")" ;
    createFieldPattern(patterns, SyntaxVariant.Named, field);

    field = "/\\*[ \t]*" + NAMED_LOC + "[ \t]*\\*/" ;
    createFieldPattern(patterns, SyntaxVariant.Named, field);

    String mock = "[ \t]*\\*/([^/\\*]*)/\\*[ \t]*";
    field = "/\\*[ \t]*\\$(" + NAME + "\\((?:" + PLAIN_ATTRIBS + ")?)" + mock + "\\)[ \t]*\\*/";
    createFieldPattern(patterns, SyntaxVariant.Named, field);

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
