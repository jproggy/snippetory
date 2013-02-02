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

public class Region implements Template, Cloneable, CharSequence, SelfAppender {
	private final Map<String, Region> children;
	private final Metadata md;
	private Region parent;
	private DataSinks data;

	public Region(Location placeHolder, List<DataSink> parts,
			Map<String, Region> children) {
		super();
		this.data = new DataSinks(parts, placeHolder);
		this.children = children;
		this.md = placeHolder.md;
		for (Region child: children.values()) {
			child.setParent(this);
		}
	}

	private Region(Region template, Location parent) {
		super();
		setParent(null);
		this.md = template.md;
		this.children = template.children;
		this.data = template.data.cleanCopy(parent);
	}

	private Region(Region template, Region parent) {
		super();
		setParent(parent);
		this.md = template.md;
		this.children = template.children;
		this.data = template.data.cleanCopy(getParentLocation());
	}

	private Location getParentLocation() {
		if (parent == null) return null;
		return parent.data.getPlaceholder();
	}

	@Override
	public Region get(String... path) {
		if (path.length == 0) return new Region(this, parent);
		Region t = getChild(path[0]);
		if (t == null) return null;
		for (int i = 1; i < path.length; i++) {
			t = t.get(path[i]);
			if (t == null) return null;
		}
		return t;
	}

	protected Region getChild(String name) {
		if (children.containsKey(name))	{
			Region child = children.get(name);
			return new Region(child, this);
		}
		Region child = data.getChild(name);
		child.setParent(this);
		return child;
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
		for (Region r : children.values()) {
			r.clear();
		}
		return this;
	}

	@Override
	public CharSequence toCharSequence() {
		return this;
	}

	@Override
    public <T extends Appendable> T appendTo(T result) {
		return data.appendTo(result);
	}

	@Override
	public String toString() {
		return appendTo(new StringBuilder()).toString();
	}

	@Override
    public String getEncoding() {
		return md.enc.getName();
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
	
	Template getParent() {
		return parent;
	}
	
	final void setParent(Region parent) {
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
	Region cleanCopy(Location parent) {
		return new Region(this, parent);
	}
}
