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

package org.jproggy.snippetory.spi;

import java.util.HashMap;
import java.util.Map;

import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.RegExSyntax;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.engine.TextPosition;
import org.jproggy.snippetory.engine.Token;
import org.jproggy.snippetory.engine.build.TemplateBuilder;
import org.jproggy.snippetory.engine.spi.FluytCCSyntax;
import org.jproggy.snippetory.engine.spi.FluytSyntax;
import org.jproggy.snippetory.engine.spi.FluytXSyntax;
import org.jproggy.snippetory.engine.spi.HiddenBlocksSyntax;
import org.jproggy.snippetory.engine.spi.UnderUnderScorySyntax;
import org.jproggy.snippetory.engine.spi.XMLAlikeSyntax;

/**
 * A syntax defines the way how Snippetory markup is determined and understood.
 * For this job it provides an {@link Tokenizer} which in turn does the real work
 * to chop the template data into handy tokens.
 *
 * @see RegExSyntax
 */
public interface Syntax {
  final class Registry {
    private final Map<String, Syntax> reg = new HashMap<>();

    private Registry() {
      register(Syntaxes.HIDDEN_BLOCKS, new HiddenBlocksSyntax());
      register(Syntaxes.XML_ALIKE, new XMLAlikeSyntax());
      register(Syntaxes.FLUYT, new FluytSyntax());
      register(Syntaxes.FLUYT_CC, new FluytCCSyntax());
      register(Syntaxes.FLUYT_X, new FluytXSyntax());
      register(Syntaxes.__SCORY, new UnderUnderScorySyntax());
    }

    public void register(SyntaxID name, Syntax syntax) {
      reg.put(name.getName(), syntax);
    }

    public Syntax byName(String name) {
      if (!reg.containsKey(name)) {
        throw new SnippetoryException("Unknown syntax: " + name);
      }
      return reg.get(name);
    }

    public Syntax getDefault() {
      return new XMLAlikeSyntax();
    }
  }

  interface Tokenizer {
    /**
     * Returns <code>true</code> if more tokens have been recognized. (In other
     * words, returns <code>true</code> if <code>next</code> would return a valid token
     * rather than throwing an exception or returning an invalid token.)
     *
     * @return <code>true</code> if the tokenizer has recognized more tokens.
     */
    boolean hasNext();

    /**
     * Returns the next token found in the input data as long as <code>hasNext</code>
     * has returned <code>true</code> immediately before this call.
     *
     * @return the next element in the iteration.
     */
    Token next();

    /**
     * @return the entire input data this tokenizer is working on.
     */
    CharSequence getData();

    /**
     *
     * @return the start position of the token the <code>next</code> method will provide
     * by its next call. Or <code>getData().length()</code> if no further token present.
     */
    int getPosition();

    /**
     * set the position where next token is expected to start. This method is mainly
     * for handing over a template from one syntax to another.
     * @param position start position of the next token
     */
    void jumpTo(int position);

    TemplateContext getContext();

    /**
     * Calculates the position of this token for error presentation.
     */
    TextPosition getPosition(Token t);
  }

  /**
   * transform input data to a stream of token. Those tokens can be used by low level tools
   * like the {@link TemplateBuilder}.
   * @param data template to be parsed as character data.
   * @return a tokenizer providing the token stream
   */
  Tokenizer parse(CharSequence data, TemplateContext ctx);

  /**
   * similar to <code>parse</code> but additionally preserves parse position
   * @param data a tokenizer, that already parsed a portion of the data.
   * @return a tokenizer providing the token stream
   */
  Tokenizer takeOver(Tokenizer data);

  /**
   * To be able to select a syntax via the <a href="/snippetory/Syntax.html#Syntax">syntax selector</a>
   * it has to be registered.
   */
  Registry REGISTRY = new Registry();

}
