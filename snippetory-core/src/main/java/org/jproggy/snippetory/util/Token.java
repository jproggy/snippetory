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

package org.jproggy.snippetory.util;

import java.util.LinkedHashMap;

import org.jproggy.snippetory.spi.Syntax;

/**
 * <p>
 * A token is a portion of the template that fulfills a special purpose, view from
 * the perspective of a syntax to parse this Snippetory template. As Snippetory
 * works with a simple syntax scheme there's only a small number of different purposes
 * such a token can fulfill. They are defined in the enum {@link TokenType}.
 * </p><p>
 * A token consists of the portion of template code it represents, the start position of
 * this code, the token type and some data that's depends on token type.
 * </p>
 *
 * @author B. Ebertz
 * @see Syntax
 */
public class Token {
  /**
   * The token type classifies the meaning of a template element.
   *
   * @author B. Ebertz
   */
  public enum TokenType {
    // start of a region
    BlockStart,
    // end of a region
    BlockEnd,
    // represents a location
    Field,
    // a syntax selector
    Syntax,
    // will be copied to output format
    TemplateData,
    // is ignored
    Comment
  }

  private final LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
  private final String name;
  private final String content;
  private final TokenType type;
  private final int position;
  private final Syntax.Tokenizer locator;

  public Token(String name, String content, TokenType type, int position, Syntax.Tokenizer locator) {
    super();
    this.name = name == null ? null : name.intern();
    this.content = content;
    this.type = type;
    this.position = position;
    this.locator = locator;
  }

  /**
   * attributes are only provided for {@link TokenType#BlockStart} and
   */
  public LinkedHashMap<String, String> getAttributes() {
    return attributes;
  }

  /**
   * Not every TokenType has a name in any case.
   * For instance the name of a field is optional and
   * comment has no name at all
   *
   * @return the name  of the element represented by this token
   *         or null or empty string if none.
   */
  public String getName() {
    return name;
  }

  /**
   * The token type categorizes different meanings of the elements
   * represented by the tokens.
   *
   * @return the token type recognized for the represented element
   */
  public TokenType getType() {
    return type;
  }

  /**
   *  The complete piece of template code representing this token. This
   *  can be used to re-assemble the original template from a token stream
   */
  public String getContent() {
    return content;
  }

  /**
   *  The position where the content starts within the entire template data.
   *  It doesn't matter where this syntax started to be used.
   */
  public int getPosition() {
    return position;
  }

  public TextPosition getTextPosition() {
    return locator.getPosition(this);
  }

  @Override
  public String toString() {
    return type + ": " + content;
  }
}
