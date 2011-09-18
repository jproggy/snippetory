/*****************************************************************************
 * Copyright (c) 2011 B. Ebertz                                              *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipes Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.jproggy.snippetory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Locale;

import org.jproggy.snippetory.impl.SnippetoryException;

/**
 * Whenever you work with Snippetory things start here. The Repo(sitory) provides access to different
 * sources of template code. May it be the simple String within your code, a file or a stream got from
 * an url. Repo will help you create the TemplateContext, and after configuration, the TemplateContext will provide
 * the template.   
 * <p>
 * For Strings there are even short cuts to directly parse the template.
 * </p>
 * @author B. Ebertz 
 */
public class Repo {
	
	
	/**
	 * The really short short cut for the simple jobs. This helps
	 * to scale from a very low level, where character hurts. At least for 
	 * playing around it's very handy.    
	 */
	public static Template parse(CharSequence data) {
		return read(data).parse();
	}

	public static Template parse(CharSequence data, Locale l) {
		return read(data).locale(l).parse();
	}

	public static TemplateContext read(CharSequence data) {
		return new TemplateContext(data);
	}
	
	/**
	 * The data for the TemplateContext is searched on class path
	 */
	public static TemplateContext readResource(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		return readResource(name, null);
	}

	public static TemplateContext readResource(String name, ClassLoader test) {
		if (name == null) {
			throw new NullPointerException();
		}
		return new TemplateContext(ToString.resource(name, test));
	}
	
	public static TemplateContext readFile(String fileName) {
		if (fileName == null) {
			throw new NullPointerException();
		}
		return new TemplateContext(ToString.file(fileName));
	}

	public static TemplateContext readFile(File fileName) {
		return new TemplateContext(ToString.file(fileName));
	}
	
	/**
	 * 
	 * @param in 
	 */
	public static TemplateContext readStream(InputStream in) {
		return new TemplateContext(ToString.stream(in));
	}

	public static TemplateContext readReader(Reader in)  {
		return new TemplateContext(ToString.reader(in));
	}
	
	private static class ToString {
		public static String resource(String name, ClassLoader test) {
			if (test != null) {
				InputStream in = test.getResourceAsStream(name);
				if (in == null) return resource(name, null);
				return stream(in);
			}
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader == null) { 
				loader = Repo.class.getClassLoader();
			}
			if (loader == null) { 
				loader = ClassLoader.getSystemClassLoader();
			}
			return stream(loader.getResourceAsStream(name));
		}
		
		public static String file(String fileName) {
			try {
				return stream(new FileInputStream(fileName));
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}
		
		public static String file(File in) {
			if (in == null) {
				return null;
			}
			try {
				return stream(new FileInputStream(in));
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}
		
		public static String stream(InputStream in) {
			if (in == null) {
				return null;
			}
			try {
				return reader(new InputStreamReader(in, "utf-8"));
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}

		public static String reader(Reader in)  {
			if (in == null) {
				return null;
			}
			try {
				char[] buffer = new char[255];
				StringWriter s = new StringWriter();
				int c;
				while ((c = in.read(buffer)) == buffer.length) {
					s.write(buffer);
				}
				if (c > 0) s.write(buffer, 0, c);
				return s.toString();
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}
	}
}
