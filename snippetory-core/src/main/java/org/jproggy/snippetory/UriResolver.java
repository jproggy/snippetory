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

import static java.util.stream.Stream.of;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jproggy.snippetory.TemplateContext.ToString;
import org.jproggy.snippetory.engine.NoDataException;
import org.jproggy.snippetory.engine.SnippetoryException;

/**
 * <p>
 * The UrlResolver allows a repository-like definition of templates
 * that are available within a template context. This offers different
 * possibilities including switching between template sets.
 * <p>
 * The User of the configured TemplateContext only needs to know a uri
 * of a template. As the TemplateContext is provided for resolution
 * schemes based on encoding or locale are possible.
 * <p>
 * Hence the UrlResolver mechanism provides a clearly bordered repository
 * while the Repo class rather provides access to the global repository.
 * <p>
 * This class already provides some basic implementations. However,
 * for the moment none of them draws the context into account. But
 * more sophisticated algorithms are likely to be based on Locale
 * or Encoding.
 *
 * @author B. Ebertz
 */
@FunctionalInterface
public interface UriResolver {
  /**
   * Creates a UriResolver that sequentially scans the provided
   * directories for a file that's represented by the uri and
   * returns the content of the first match.
   */
  static UriResolver directories(String... dirs) {
    return directories(of(dirs).map(File::new).toArray(File[]::new));
  }

  /**
   * Creates a UriResolver that sequentially scans the provided
   * directories for a file that's represented by the uri and
   * returns the content of the first match.
   */
  static UriResolver directories(File... dirs) {
    return (uri, context) -> {
      for (File dir : dirs) {
        File test = new File(dir, uri);
        if (test.exists()) return ToString.file(test);
      }
      throw new NoDataException(uri + " not found.");
    };
  }

  /**
   * Resolves to a resource. The resource has to be queried as described
   * in {@link ClassLoader#getResource(String)}. The used Classloader
   * is typically the {@link Thread#getContextClassLoader() ContextClassloader}
   */
  static UriResolver resource() {
    return (resource, context) -> ToString.resource(resource);
  }

  /**
   * Allows resource lookup from packages with a short name as well
   * as organizing template sets in different packages.
   */
  static UriResolver resource(String prefix) {
    String realPF = (prefix.isEmpty() || prefix.endsWith("/")) ? prefix : (prefix + '/');
    return (String resource, TemplateContext context) -> {
      String path = realPF + (resource.startsWith("/") ? resource.substring(1) : resource);
      return ToString.resource(path);
    };
  }

  /**
   * resolves uris relative to the base URL
   */
  static UriResolver url(String base) {
    try {
      return url(new URL(base));
    } catch (MalformedURLException e) {
      throw new SnippetoryException(e);
    }
  }

  /**
   * resolves uris relative to the base URL
   */
  static UriResolver url(URL base) {
    return (String url, TemplateContext context) -> {
      try {
        URL link = new URL(base, url);
        return ToString.stream(link.openStream());
      } catch (IOException e) {
        throw new SnippetoryException(e);
      }
    };
  }

  /**
   * Build a more complex repository consisting of several other, searched in order
   * for and uri.
   */
  static RepoBuilder combine() {
    return new RepoBuilder();
  }

  /**
   * Resolves the uri within the repository to the data represented
   * by it. This data will be parsed to a template.
   * The meaning of the uri may greatly differ depending on the
   * implementation. It may or may not map to a certain location.
   */
  String resolve(String uri, TemplateContext context);
}
