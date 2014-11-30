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

public class HiddenBlocksSyntax extends RegExSyntax {

  @Override
  public RegexParser parse(CharSequence data, TemplateContext ctx) {
    Map<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

    String pref = "(?:\\<\\!\\-\\-|\\/\\*)";
    String suff = "[ \\t]*(?:\\-\\-\\>|\\*\\/)";

    patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);
    Pattern start = Pattern.compile(LINE_START + pref + "t\\:(" + NAME + ATTRIBUTES + ")?" + suff + LINE_END,
        Pattern.MULTILINE);
    patterns.put(start, TokenType.BlockStart);

    start = Pattern.compile(pref + "t\\:(" + NAME + ATTRIBUTES + ")?" + suff);
    patterns.put(start, TokenType.BlockStart);

    Pattern end = Pattern.compile(LINE_START + pref + "\\!t\\:(" + NAME + ")?" + suff + LINE_END, Pattern.MULTILINE);
    patterns.put(end, TokenType.BlockEnd);

    end = Pattern.compile(pref + "\\!t\\:(" + NAME + ")?" + suff);
    patterns.put(end, TokenType.BlockEnd);

    Pattern field = Pattern.compile("\\{v\\:(" + NAME + ATTRIBUTES + ")[ \\t]*\\}");
    patterns.put(field, TokenType.Field);

    Pattern nameless = Pattern.compile("\\{v\\:[ \\t]*(" + ATTRIBUTE + ATTRIBUTES + ")[ \\t]*\\}");
    patterns.put(nameless, TokenType.Field);
    return new RegexParser(data, ctx, patterns);
  }
}
