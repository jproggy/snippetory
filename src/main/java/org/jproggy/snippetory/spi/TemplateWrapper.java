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

package org.jproggy.snippetory.spi;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Set;

import org.jproggy.snippetory.Template;

public abstract class TemplateWrapper implements Template {
	protected final Template wrapped;
	
	public TemplateWrapper(Template template) {
		this.wrapped = template;
	}

	public String getEncoding() {
		return wrapped.getEncoding();
	}

	public CharSequence toCharSequence() {
		return wrapped.toCharSequence();
	}

	public Template get(String... name) {
		if (name.length == 0) return this;
		return wrap(wrapped.get(name));
	}
	
	protected abstract Template wrap(Template toBeWrapped);

	public Template set(String name, Object value) {
		wrapped.set(name, value);
		return this;
	}

	public Template append(String name, Object value) {
		wrapped.append(name, value);
		return this;
	}

	public Template clear() {
		wrapped.clear();
		return this;
	}

	public void render() {
		wrapped.render();
	}

	public void render(String siblingName) {
		wrapped.render(siblingName);
	}

	public void render(Template target, String name) {
		wrapped.render(target, name);
	}

	public void render(Writer out) throws IOException {
		wrapped.render(out);
	}

	public void render(PrintStream out) throws IOException {
		wrapped.render(out);
	}

	public Set<String> names() {
		return wrapped.names();
	}

	public Set<String> regionNames() {
		return wrapped.regionNames();
	}
	
	public String toString() {
		return wrapped.toString();
	}
}
