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

package org.jproggy.snippetory;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

import org.jproggy.snippetory.TemplateContext.ToString;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;

/**
 * Whenever you work with Snippetory things start here. The Repo(sitory)
 * provides access to different sources of template code. May it be the simple
 * String within your code, a file or a stream got from an url. Repo will help
 * you create the TemplateContext, and after configuration, the TemplateContext
 * will provide the template.
 * <p>
 * For Strings there are even short cuts to directly parse the template.
 * </p>
 * However this is rather for simple usage like tests or examples. Whenever things
 * get complicated consider use of {@link org.jproggy.snippetory.TemplateContext}
 *
 * @see org.jproggy.snippetory.TemplateContext TemplateContext
 * @see Template
 *
 * @author B. Ebertz
 */
public class Repo {
  private Repo() {
    super();
  }

  /**
   * Short cut method for parsing a string into a template. With default syntax,
   * null encoding and no locale support. <br />
   * This is sometimes useful for very short and simple templates replacing
   * a complex string concatenation.
   *
   * @deprecated prefer defining a syntax when parsing. I.e. use
   * {@link Syntaxes#parse(CharSequence) Syntaxes.XML_ALIKE.parse(CharSequence)} instead
   */
  public static Template parse(CharSequence data) {
    return new TemplateContext(data).parse();
  }

  /**
   * Short cut method for parsing a string into a template. With default syntax,
   * null encoding, but supporting a locale for formatting.
   *
   * @deprecated prefer defining a syntax when parsing. I.e. use
   * {@link Syntaxes#parse(CharSequence, Locale) Syntaxes.XML_ALIKE.parse(CharSequence, Locale)} instead
   */
  public static Template parse(CharSequence data, Locale l) {
    return new TemplateContext(data).locale(l).parse();
  }

  /**
   * The really short short cut for the simple jobs. This helps to scale from
   * a very low level, where any character hurts. At least for playing around
   * it's very handy.
   */
  public static TemplateContext read(CharSequence data) {
    return new TemplateContext(data);
  }

  /**
   * The data for the TemplateContext is searched on class path
   */
  public static TemplateContext readResource(String name) {
    return new TemplateContext(ToString.resource(name));
  }

  public static TemplateContext readFile(String fileName) {
    return new TemplateContext(ToString.file(fileName));
  }

  public static TemplateContext readFile(File fileName) {
    return new TemplateContext(ToString.file(fileName));
  }

  /**
   * Reads the complete content of the stream considering it to utf-8
   * encoded. Once read the stream is closed.
   *
   * @param in a stream providing data representing a Snippetory
   * template.
   * @return a TemplateContext containing the data of the stream.
   */
  public static TemplateContext readStream(InputStream in) {
    return new TemplateContext(ToString.stream(in));
  }

  /**
   * reads the complete data of the reader. Once read the reader
   * is closed.
   *
   * @param in a stream providing data representing a Snippetory
   * template.
   * @return a TemplateContext containing the data of the stream.
   */
  public static TemplateContext readReader(Reader in) {
    return new TemplateContext(ToString.reader(in));
  }

  /**
   * Repo uses a special TemplateContext that replaces the functionality
   * of configuring a UrlResolver by the ability to keep the data provided
   * during initialization.
   */
  public static class TemplateContext extends org.jproggy.snippetory.TemplateContext {
    private final CharSequence data;

    private TemplateContext(CharSequence data) {
      super();
      this.data = data;
    }

    @Override
    public TemplateContext locale(Locale locale) {
      return (TemplateContext)super.locale(locale);
    }

    @Override
    public TemplateContext attrib(String name, String value) {
      return (TemplateContext)super.attrib(name, value);
    }

    @Override
    public TemplateContext encoding(Encoding encoding) {
      return (TemplateContext)super.encoding(encoding);
    }

    @Override
    public TemplateContext encoding(String encoding) {
      return (TemplateContext)super.encoding(encoding);
    }

    @Override
    public TemplateContext syntax(Syntax syntax) {
      return (TemplateContext)super.syntax(syntax);
    }

    @Override
    public TemplateContext syntax(SyntaxID syntax) {
      return (TemplateContext)super.syntax(syntax);
    }

    /**
     * @deprecated Configuring the resolver is not allowed within Repo.
     */
    @Override
    @Deprecated
    public void setUriResolver(UriResolver urlResolver) {
      throw new UnsupportedOperationException("UrlResolver can't be set here");
    }

    public Template parse() {
      return parse(data);
    }
  }
}
