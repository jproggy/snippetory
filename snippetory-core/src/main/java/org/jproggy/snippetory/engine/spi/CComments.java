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

/**
 * C_COMMENTS syntax uses C comment areas and C++ line comments to hide template syntax
 * from compilers and interpreters that might be used to validate the templates.
 * <br />
 * The template syntax is based on a leading $ sign and braces to mark the area
 * of impact. On regions the name is repeated at the end, while pure locations
 * simply close the curly bracket.
 * <br />
 * A special variant of the syntax allows mocking:
 * <br />
 * <code style="color:darkblue;">
 * /*${name attrib="value"&#42;/<b>mock</b>/*}&#42;/;
 * </code>
 * <br />
 * The mock will be ignored and will not be written to the output. This supports
 * to keep the template valid for execution or compilation as for the validation
 * environment the mock is visible and substitutes data to be bound.
 * <br />
 * Line breaks are supported whitespace in C_COMMENTS but not within the mock.
 *
 * @author B. Ebertz
 */
public class CComments extends RegExSyntax {

  @Override
  public RegexParser parse(CharSequence data, TemplateContext ctx) {
    Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

    patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);
    String commentBlockStart = "\\/\\*\\s*";
    String commentBlockEnd = "\\s*\\*\\/";

    String pre = commentBlockStart + "\\$\\{\\s*";
    String suff = "\\s*\\}" + commentBlockEnd;

    String ATTRIBUTES = "(?:\\s+" + ATTRIBUTE + ")*)";

    Pattern mock_default = Pattern.compile(pre + "(" + NAME + ATTRIBUTES + commentBlockEnd + "((?:(?!/\\*).)*)"
        + commentBlockStart + suff, Pattern.MULTILINE);
    patterns.put(mock_default, TokenType.Field);

    Pattern start = Pattern.compile(LINE_START + "\\/\\/\\s*\\$\\{[\\s/]*(" + NAME + "(?:\\s[\\s/]*" + ATTRIBUTE
        + ")*)" + LINE_END, Pattern.MULTILINE);
    patterns.put(start, TokenType.BlockStart);

    start = Pattern.compile(pre + "(" + NAME + ATTRIBUTES + commentBlockEnd, Pattern.MULTILINE);
    patterns.put(start, TokenType.BlockStart);

    Pattern end = Pattern.compile(LINE_START + "\\/\\/\\s*(" + NAME + ")\\s*\\}" + LINE_END, Pattern.MULTILINE);
    patterns.put(end, TokenType.BlockEnd);

    end = Pattern.compile(commentBlockStart + "(" + NAME + ")\\s*\\}" + commentBlockEnd, Pattern.MULTILINE);
    patterns.put(end, TokenType.BlockEnd);

    Pattern comment = Pattern.compile(LINE_START + "\\/\\/\\/.*" + LINE_END, Pattern.MULTILINE);
    patterns.put(comment, TokenType.Comment);

    Pattern field = Pattern.compile(pre + "(" + NAME + ATTRIBUTES + suff, Pattern.MULTILINE);
    patterns.put(field, TokenType.Field);

    Pattern nameless = Pattern.compile(pre + "(" + ATTRIBUTE + ATTRIBUTES + suff, Pattern.MULTILINE);
    patterns.put(nameless, TokenType.Field);
    return new RegexParser(data, ctx, patterns);
  }
}
