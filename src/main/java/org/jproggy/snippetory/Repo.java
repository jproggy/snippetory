/*****************************************************************************
 * Copyright (c) 2011 B. Ebertz                                              *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipes Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.jproggy.snippetory;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

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
	 * to scale from a very low level, where any character hurts. At least for 
	 * playing around it's very handy.    
	 */
	public static Template parse(CharSequence data) {
		return new TemplateContext().data(data).parse();
	}

	public static Template parse(CharSequence data, Locale l) {
		return new TemplateContext().data(data).locale(l).parse();
	}

	public static TemplateContext read(CharSequence data) {
		return new TemplateContext().data(data);
	}
	
	/**
	 * The data for the TemplateContext is searched on class path
	 */
	public static TemplateContext readResource(String name) {
		return new TemplateContext().readResource(name);
	}

	public static TemplateContext readResource(String name, ClassLoader test) {
		return new TemplateContext().readResource(name, test);
	}
	
	public static TemplateContext readFile(String fileName) {
		return new TemplateContext().readFile(fileName);
	}

	public static TemplateContext readFile(File fileName) {
		return new TemplateContext().readFile(fileName);
	}
	
	/**
	 * 
	 * @param in 
	 */
	public static TemplateContext readStream(InputStream in) {
		return new TemplateContext().readStream(in);
	}

	public static TemplateContext readReader(Reader in)  {
		return new TemplateContext().readReader(in);
	}
}
