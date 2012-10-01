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

package org.jproggy.snippetory.engine;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.chars.SelfAppender;
import org.jproggy.snippetory.spi.Encoding;

public class Region implements Template, Cloneable, CharSequence, SelfAppender {
	private final Map<String, ? extends Template> children;
	private final Metadata md;
	private Template parent;
	private DataSinks data;

	public Region(Location placeHolder, List<DataSink> parts,
			Map<String, Region> children) {
		super();
		this.data = new DataSinks(parts);
		this.children = children;
		this.md = placeHolder.md;
		for (Region child: children.values()) {
			child.setParent(this);
		}
	}

	private Region(Region template) {
		super();
		this.md = template.md;
		this.children = template.children;
		this.data = template.data.clone();
	}

	@Override
	public Template get(String... path) {
		if (path.length == 0)
			return this;
		Template t = children.get(path[0]);
		if (t == null)
			return null;
		if (path.length == 1) {
			Region copy = new Region((Region)t);
			copy.setParent(this);
			return copy;
		}
		for (int i = 1; i < path.length; i++) {
			t = t.get(path[i]);
			if (t == null)
				return null;
		}
		return t;
	}
	
	@Override
	public Region set(String key, Object value) {
		data.set(key, value);
		return this;
	}

	@Override
	public Region append(String key, Object value) {
		data.append(key, value);
		return this;
	}

	@Override
	public Region clear() {
		data.clear();
		return this;
	}

	@Override
	public CharSequence toCharSequence() {
		return this;
	}

	public <T extends Appendable> T appendTo(T result) {
		return data.appendTo(result);
	}

	@Override
	public String toString() {
		return appendTo(new StringBuilder()).toString();
	}

	public String getEncoding() {
		Encoding e = md.enc;
		if (e == null)
			return null;
		return e.getName();
	}

	@Override
	public void render() {
		// ignore render calls on root node as they don't make any sense.
		if (isRoot()) return;
		render(md.name);
	}

	private boolean isRoot() {
		return getParent() == null;
	}

	@Override
	public void render(String target) {
		render(getParent(), target);
	}

	@Override
	public void render(Template target, String key) {
		target.append(key, this);
	}

	@Override
	public void render(Writer out) throws IOException {
		appendTo(out);
		out.flush();
	}

	@Override
	public void render(PrintStream out) throws IOException {
		appendTo(out);
		out.flush();
	}

	@Override
	public Set<String> names() {
		return data.names();
	}

	@Override
	public Set<String> regionNames() {
		return children.keySet();
	}
	
	public Template getParent() {
		return parent;
	}
	
	public void setParent(Template parent) {
		this.parent = parent;
	}

	@Override
	public int length() {
		return data.length();
	}

	@Override
	public char charAt(int index) {
		return data.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return data.subSequence(start, end);
	}
}
