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
public abstract class UriResolver {
	/**
	 * Creates a UriResolver that sequentially scans the provided
	 * directories for a file that's represented by the uri and
	 * returns the content of the first match.
	 */
	public static UriResolver directories(final String... dirs) {
		File[] files = new File[dirs.length];
		for (int i = 0; i < dirs.length; i++) {
			files[i] = new File(dirs[i]);
		}
		return directories(files);
	}
	/**
	 * Creates a UriResolver that sequentially scans the provided
	 * directories for a file that's represented by the uri and
	 * returns the content of the first match.
	 */
	public static UriResolver directories(final File... dirs) {
		return new UriResolver() {

			@Override
			public String resolve(String uri, TemplateContext context) {
				for (File dir :dirs) {
					File test = new File(dir, uri);
					if (test.exists()) return ToString.file(test);
				}
				throw new NoDataException(uri + " not found.");
			}
		};
	}
	/**
	 * Resolves to a resource. The resource has to be queried as described
	 * in {@link ClassLoader#getResource(String)}. The used Classloader
	 * is typically the {@link Thread#getContextClassLoader() ContextClassloader}
	 *
	 */
	public static UriResolver resource() {
		return new UriResolver() {

			@Override
			public String resolve(String resource, TemplateContext context) {
				return ToString.resource(resource);
			}
		};
	}
	/**
	 * Allows resource lookup from packages with a short name as well
	 * as organizing template sets in different packages.
	 */
	public static UriResolver resource(String prefix) {
		final String realPF = (prefix.isEmpty() || prefix.endsWith("/")) ? prefix : prefix + '/';
		return new UriResolver() {

			@Override
			public String resolve(String resource, TemplateContext context) {
				String path = realPF + (resource.startsWith("/") ? resource.substring(1) : resource);
				return ToString.resource(path);
			}
		};
	}
	/**
	 * resolves uris relative to the base URL
	 */
	public static UriResolver url(String base) {
		try {
			return url(new URL(base));
		} catch (MalformedURLException e) {
			throw new SnippetoryException(e);
		}
	}
	/**
	 * resolves uris relative to the base URL
	 */
	public static UriResolver url(final URL base) {
		return new UriResolver() {

			@Override
			public String resolve(String url, TemplateContext context) {
				try {
					URL link = new URL(base, url);
					return ToString.stream(link.openStream());
				} catch (IOException e) {
					throw new SnippetoryException(e);
				}
			}
		};
	}

	/**
	 * Build a more complex repository consisting of several other, searched in order
	 * for and uri.
	 */
	public static RepoBuilder combine() {
	  return new RepoBuilder();
	}

	/**
	 * Resolves the uri within the repository to the data represented
	 * by it. This data will be parsed to a template.
	 * The meaning of the uri may greatly differ depending on the
	 * implementation. It may or may not map to a certain location.
	 */
	public abstract String resolve(String uri, TemplateContext context);
}
