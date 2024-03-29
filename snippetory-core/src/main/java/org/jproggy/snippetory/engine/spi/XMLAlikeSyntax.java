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

import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.util.Token.TokenType;

public class XMLAlikeSyntax extends JBSyntax {
  public static final Pattern NAMESPACE_URI = Pattern.compile(
      " xmlns:t=\"http://www\\.jproggy\\.org/snippetory/[a-zA-Z_]++\\.xsd\"");

  @Override
  protected void addComments(Map<Pattern, TokenType> patterns) {
    super.addComments(patterns);
    patterns.put(NAMESPACE_URI, TokenType.Comment);
  }
}
