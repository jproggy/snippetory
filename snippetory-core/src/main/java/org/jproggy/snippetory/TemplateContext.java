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

import static java.util.Collections.unmodifiableMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.engine.SyntaxRegistry;
import org.jproggy.snippetory.engine.build.TemplateBuilder;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;
import org.jproggy.snippetory.util.NoDataException;

/**
 * <p>The TemplateContext represents the configuration how templates are parsed. It
 * provides a fluent interface for inline creation as well as a bean interface
 * for convenient injection. Using the injection variant it's also easy to use a
 * subclass and use a different parsing mechanism or place an additional layer
 * of interceptors.
 * </p>
 *
 * <p>In fact the TemplateContext is designed to be extended to be integrated into
 * your global data. For example some applications configure presentation attributes
 * like number formatting at user level. In this case you can easily add the user
 * to the TemplateContext and replace the numb Formatter by one the values the user.
 * </p>
 *
 * <p><strong>Caution:</strong> As the TemplateContext is a mutable construct it has to
 * be considered single threaded! For reuse over several threads one would need to
 * ensure immutability. However, the clone method allows fast creation of copies.
 * </p>
 */
public class TemplateContext implements Cloneable {
  public static final Locale TECH = new Locale("en", "US", "tech");
  private Locale locale = TECH;
  private Syntax syntax = SyntaxRegistry.INSTANCE.getDefault();
  private UriResolver uriResolver;

  /**
   * Omit initialization here to ensure TemplateContext to be cheap in default behavior
   * and generate the cost of a map only when required.
   */
  private Map<String, String> baseAttribs;

  private static final Map<String, String> DEFAULT_ATTRIBUTES;

  static {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("date", "");
    map.put("int", "");
    map.put("decimal", "");
    map.put("null", "");
    DEFAULT_ATTRIBUTES = unmodifiableMap(map);

    TemplateBuilder.init();
  }

  @Override
  public TemplateContext clone() {
    try {
      TemplateContext result = (TemplateContext)super.clone();
      if (baseAttribs != null) result.baseAttribs = new HashMap<>(baseAttribs);
      return result;
    } catch (CloneNotSupportedException e) {
      throw new SnippetoryException(e);
    }
  }

  public TemplateContext syntax(SyntaxID syntax) {
    return syntax(SyntaxRegistry.INSTANCE.byName(syntax.getName()));
  }

  public TemplateContext syntax(Syntax syntax) {
    setSyntax(syntax);
    return this;
  }

  public Syntax getSyntax() {
    return syntax;
  }

  public void setSyntax(Syntax syntax) {
    if (syntax == null) throw new NullPointerException();
    this.syntax = syntax;
  }

  public TemplateContext uriResolver(UriResolver uriResolver) {
    setUriResolver(uriResolver);
    return this;
  }

  public void setUriResolver(UriResolver uriResolver) {
    this.uriResolver = uriResolver;
  }

  public UriResolver getUriResolver() {
    return uriResolver;
  }

  public TemplateContext encoding(String encoding) {
    return attrib("enc", encoding);
  }

  public TemplateContext encoding(Encoding encoding) {
    return encoding(encoding.getName());
  }

  public TemplateContext locale(Locale locale) {
    this.locale = locale;
    return this;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public TemplateContext attrib(String name, String value) {
    if (baseAttribs == null) {
      initBaseAttribs();
    }
    this.baseAttribs.put(name, value);
    return this;
  }

  protected void initBaseAttribs() {
    baseAttribs = new LinkedHashMap<>(DEFAULT_ATTRIBUTES);
  }

  /**
   * Returns the attributes intended to be set on the root node of a template created
   * by one of the parse methods. This makes especially sense for inherited attributes.
   * Expect the returned map to be unmodifiable. I.e. copy via copy constructor and
   * set newly if you want to extend it.
   */
  public Map<String, String> getBaseAttribs() {
    if (baseAttribs == null) return DEFAULT_ATTRIBUTES;
    return unmodifiableMap(baseAttribs);
  }

  public void setBaseAttribs(Map<String, String> baseAttribs) {
    this.baseAttribs = new HashMap<>(baseAttribs);
  }

  /**
   * Get the Template identified by the uri. The uri is resolved by the configured uri resolver.
   * Thus configuring a UriResolver is mandatory,
   */
  public Template getTemplate(String uri) {
    if (uriResolver == null) {
      throw new IllegalStateException("Need UrlResolver to find Template. Please set one");
    }
    return parse(uriResolver.resolve(uri, this));
  }

  public Template parse(CharSequence data) {
    return TemplateBuilder.parse(this, data);
  }

  static class ToString {
    private ToString() {}

    public static String resource(String name) {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      if (loader == null) {
        loader = ToString.class.getClassLoader();
      }
      if (loader == null) {
        loader = ClassLoader.getSystemClassLoader();
      }
      InputStream stream = loader.getResourceAsStream(name);
      if (stream == null) throw new NoDataException("Resource " + name + " not found");
      return stream(stream);
    }

    public static String file(String fileName) {
      try {
        return stream(new FileInputStream(fileName));
      } catch (FileNotFoundException e) {
        throw new NoDataException(e);
      }
    }

    public static String file(File in) {
      try {
        return stream(new FileInputStream(in));
      } catch (FileNotFoundException e) {
        throw new NoDataException(e);
      }
    }

    public static String stream(InputStream in) {
      return reader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public static String reader(Reader in) {
      try {
        try (in) {
          char[] buffer = new char[255];
          StringWriter s = new StringWriter();
          int c;
          while ((c = in.read(buffer)) >= 0) {
            s.write(buffer, 0, c);
          }
          return s.toString();
        }
      } catch (IOException e) {
        throw new SnippetoryException(e);
      }
    }
  }
}
