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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.chars.CharSequences;
import org.jproggy.snippetory.spi.Encoding;

public class Region extends CharSequences implements Template, Cloneable {
	private final Object[] parts;
	private final Map<String, ? extends Template> children;
	private final Metadata md;
	private Template parent;

	public Region(Location placeHolder, List<Object> parts,
			Map<String, Region> children) {
		super();
		this.parts = parts.toArray();
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
		this.parts = new Object[template.parts.length];
		for (int i = 0; i < parts.length; i++) {
			Object p = template.parts[i]; 
			if (p instanceof String) {
				parts[i] = p;
			} else {
				parts[i] = new Location((Location)p);
			}
		}
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
	
	private Iterable<Location> byName(final String name) {
		return new PartFilter() {
			@Override
			public boolean fits(Location part) {
				return name.equals(part.getName());
			}
		};
	}

	private Iterable<Location> locations() {
		return new PartFilter();
	}

	private class PartFilter implements Iterable<Location> {
		public Iterator<Location> iterator() {
			return new Iterator<Location>() {
				int pos = 0;
				Location recent = init();

				@Override
				public boolean hasNext() {
					return recent != null;
				}

				private Location init() {
					while (pos < parts.length) {
						Object part = parts[pos++];
						if (part instanceof Location) {
							Location loc = (Location) part;
							if (fits(loc)) return loc;
						}
					}
					return null;
				}

				public Location next() {
					Location next = recent;
					recent = init();
					return next;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		public boolean fits(Location part) { return true; }
	};

	@Override
	public Region set(String key, Object value) {
		for (Location v : byName(key)) {
			v.set(value);
		}
		return this;
	}

	@Override
	public Region append(String key, Object value) {
		for (Location v : byName(key)) {
			v.append(value);
		}
		return this;
	}

	@Override
	public Region clear() {
		for (Location v : locations()) {
			v.clear();
		}
		return this;
	}

	@Override
	public CharSequence toCharSequence() {
		return this;
	}

	public <T extends Appendable> T appendTo(T result) {
		try {
			for (Object part : parts) {
				if (part instanceof Location) {
					result.append(((Location) part).toCharSequence());
				} else {
					result.append((String) part);
				}
			}
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
		return result;
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
		render(md.name);
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
		Set<String> result = new TreeSet<String>();
		for (Object part : parts) {
			if (part instanceof Location && ((Location) part).getName()  != null) {
				result.add(((Location) part).getName());
			}
		}
		return result;
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
	protected CharSequence part(int index) {
		Object p = parts[index];
		return p instanceof Location ? ((Location)p).toCharSequence() : (String)p;
	}
	
	@Override
	protected int partCount() {
		return parts.length;
	}
}
