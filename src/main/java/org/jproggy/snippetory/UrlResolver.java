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
 * 
 * 
 * @author B. Ebertz
 */
public abstract class UrlResolver { 
	public static UrlResolver directories(final String... dirs) {
		File[] files = new File[dirs.length];
		for (int i = 0; i < dirs.length; i++) {
			files[i] = new File(dirs[i]);
		}
		return directories(files);
	}
	public static UrlResolver directories(final File... dirs) {
		return new UrlResolver() {
			
			@Override
			public String resolve(String url) {
				for (File dir :dirs) {
					File test = new File(dir, url);
					if (test.exists()) return ToString.file(test);
				}
				throw new NoDataException(url + " not found.");
			}
		};
	}
	public static UrlResolver resource() {
		return new UrlResolver() {
			
			@Override
			public String resolve(String url) {
				return ToString.resource(url, null);
			}
		};
	}
	public static UrlResolver url(String base) {
		try {
			return url(new URL(base));
		} catch (MalformedURLException e) {
			throw new SnippetoryException(e);
		}
	}
	public static UrlResolver url(final URL base) {
		return new UrlResolver() {
			
			@Override
			public String resolve(String url) {
				try {
					URL link = new URL(base, url);
					return ToString.stream(link.openStream());
				} catch (IOException e) {
					throw new SnippetoryException(e);
				}
			}
		};
	}
	public abstract String resolve(String url);
}
