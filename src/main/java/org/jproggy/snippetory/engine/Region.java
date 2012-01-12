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
import org.jproggy.snippetory.spi.Encoding;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Region implements Template, Cloneable, CharSequence {
	private Object[] parts;
	private Map<String, Template> children;
	private final Location placeHolder; 

	public Region(Location placeHolder, List<Object> parts, Map<String, Template> children) {
		this.parts = parts.toArray();
		this.children = children;
		this.placeHolder = placeHolder;
	}

	@Override
	public Template get(String... path) {
		if (path.length == 0) return this;
		Template t = children.get(path[0]);
		if (t == null) return null;
		for (int i = 1; i < path.length; i++) {
			t = t.get(path[i]);
			if (t == null) return null;
		}
		return t.clear();
	}
	
	private Iterable<Location> byName(final String name) {
		return new PartFilter() {
			@Override
			public boolean fits(Location part) {
				return part.getName().equals(name);
			}			
		};
	}

	private Iterable<Location> locations() {
		return new PartFilter() {
			@Override
			public boolean fits(Location part) {
				return true;
			}			
		};
	}
	
	private abstract class PartFilter implements Iterable<Location> {
		public Iterator<Location> iterator() {
			return new Iterator<Location>() {
				int pos = 0;
				Location recent =  init();
				@Override
				public boolean hasNext() {
					return recent != null;
				}
				private Location init() {
					while (pos < parts.length) {
						Object part = parts[pos++];
						if (part instanceof Location && fits((Location)part)) {
							return (Location)part;
						}
					}
					return null;
				}
				public Location next() {
					Location next =  recent;
					recent = init();
					return next;
				}
				public void remove() { throw new UnsupportedOperationException(); }
			};
		}
		public abstract boolean fits(Location part);
	};


	@Override
	public Region set(String key, Object value) {
		for (Location v: byName(key)) {
			v.set(value);
		}
		return this;
	}

	@Override
	public Region append(String key, Object value) {
		for (Location v: byName(key)) {
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

	void append(StringBuilder result) {
		for (Object part : parts) {
			if (part instanceof Location) {
				result.append(((Location)part).toCharSequence());
			} else {
				result.append(part);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		append(result);
		return result.toString();
	}

	public String getEncoding() {
		Encoding e = placeHolder.getEncoding();
		if (e == null) return null;
		return e.getName();
	}

	@Override
	public void render() {
		placeHolder.append(this);
	}

	@Override
	public void render(String target) {
		render(placeHolder.getParent().getTemplate(), target);
	}

	@Override
	public void render(Template target, String key) {
		target.append(key, this);
	}
	
	@Override
	public void render(Writer out) throws IOException {
		out.append(this.toString());
		out.flush();
	}
	
	@Override
	public void render(PrintStream out) throws IOException {
		out.append(toString());
		out.flush();
	}
	
	@Override
	public Set<String> names() {
		Set<String> result =  new TreeSet<String>();
		for (Object part: parts) {
			if (part instanceof Location) {
				result.add(((Location)part).getName());
			}
		}
		return result;
	}
	
	@Override
	public Set<String> regionNames() {
		return children.keySet();
	}
	
	@Override
	public char charAt(int index) {
		throw new NotImplementedException();
	}
	
	@Override
	public int length() {
		int l = 0;
		for (Object part : parts) {
			if (part instanceof Location) {
				l += ((Location)part).toCharSequence().length();
			} else {
				l += part.toString().length();
			}
		}
		return l;
	}
	
	@Override
	public CharSequence subSequence(int start, int end) {
		return new MyCharSeq(start, end);
	}
	
	private class MyCharSeq implements CharSequence {
		private final int start, end;
		public MyCharSeq(int start, int end) {
			this.start = start;
			this.end = end;
		}
		@Override
		public char charAt(int index) {
			return Region.this.charAt(index + start);
		}
		
		@Override
		public int length() {
			return end - start;
		}
		
		@Override
		public CharSequence subSequence(int start, int end) {
			return new MyCharSeq(start + this.start, end + this.start);
		}
	}
}
