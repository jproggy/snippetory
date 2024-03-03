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

import org.jproggy.snippetory.util.Token.TokenType;

import java.util.Map;
import java.util.regex.Pattern;

public class FluytXASyntax extends FluytSyntax {
  public static final Pattern NAMESPACE_URI = Pattern.compile(
      " xmlns:t=\"http://www\\.jproggy\\.org/snippetory/[a-zA-Z_]++\\.xsd\"");

  @Override
  protected Map<Pattern, TokenType> createPatterns() {
    Map<Pattern, TokenType> patterns = super.createPatterns();
    new XMLAlikeSyntax().addRegions(patterns);
    new XMLAlikeSyntax().addLocations(patterns);
    patterns.put(NAMESPACE_URI, TokenType.Comment);
    return patterns;
  }
}
