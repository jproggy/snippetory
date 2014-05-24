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
    Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

    patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);

    createBlockPattern(patterns, TokenType.BlockStart, START_TOKEN);
    createInlinePattern(patterns, TokenType.BlockStart, START_TOKEN);

    createBlockPattern(patterns, TokenType.BlockEnd, END_TOKEN);
    createInlinePattern(patterns, TokenType.BlockEnd, END_TOKEN);

    String field = "\\:(" + NAME + "/\\*(?:" + PLAIN_ATTRIBS + ")?)\\*/";
    createFieldPattern(patterns, SyntaxVariant.Named, field);

    field = "\\:(" + NAME + ")" ;
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
