/*******************************************************************************
 * Copyright (c) 2011-2012 JProggy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR 
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE, 
 * NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE
 *******************************************************************************/
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
		final String realPF = prefix.endsWith("/") ? prefix : prefix + '/';
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
	 * Resolves the uri within the repository to the data represented
	 * by it. This data will be parsed to a template.
	 * The meaning of the uri may greatly differ depending on the 
	 * implementation. It may or may not map to a certain location.
	 */
	public abstract String resolve(String uri, TemplateContext context);
}
