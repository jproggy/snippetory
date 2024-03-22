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

package org.jproggy.snippetory.cypher;

import org.jproggy.snippetory.cypher.impl.CypherSyntax;
import org.jproggy.snippetory.spi.Configurer;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;
import org.jproggy.snippetory.util.EncodedContainer;
import org.jproggy.snippetory.util.IncompatibleEncodingException;

import java.io.IOException;

public class Cypher implements Encoding, Configurer {
  public static final SyntaxID SYNTAX;
  static {
    CypherSyntax s = new CypherSyntax();
    SYNTAX = s;
    Syntax.register(SYNTAX, s);
  }

  public static final Cypher ENCODING = new Cypher();
  static {
    Encoding.register(ENCODING);
  }

  public Cypher() {
    super();
  }

  public static EncodedData markAsCypher(CharSequence value) {
    return new EncodedContainer(value, ENCODING.getName());
  }

  @Override
  public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException,
          IncompatibleEncodingException {
    if (value.length() == 1 && value.charAt(0) == '?') {
      target.append('?');
    } else {
      throw new IncompatibleEncodingException("can't convert encoding " + encodingName + " into " + getName());
    }
  }

  @Override
  public String getName() {
    return "cypher";
  }
}
