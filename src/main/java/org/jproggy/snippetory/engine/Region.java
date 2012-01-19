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

public class Region implements Template, Cloneable, CharSequence {
	private Object[] parts;
	private Map<String, Template> children;
	private final Location placeHolder;

	public Region(Location placeHolder, List<Object> parts,
			Map<String, Template> children) {
		this.parts = parts.toArray();
		this.children = children;
		this.placeHolder = placeHolder;
	}

	private Region(Location placeHolder, Region template) {
		this.placeHolder = placeHolder;
		this.children = template.children;
		this.parts = new Object[template.parts.length];
		for (int i = 0; i < parts.length; i++) {
			Object p = template.parts[i]; 
			if (p instanceof String) {
				parts[i] = p;
			} else {
				parts[i] = new Location(placeHolder, (Location)p);
			}
		}
		placeHolder.setTemplate(this);
	}

	@Override
	public Template get(String... path) {
		if (path.length == 0)
			return this;
		Template t = children.get(path[0]);
		if (t == null)
			return null;
		if (path.length == 1) {
			return new Region(byName(path[0]).iterator().next(), (Region)t);
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
				return part.getName().equals(name);
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

	public void append(Appendable result) {
		try {
			for (Object part : parts) {
				if (part instanceof Location) {
					result.append(((Location) part).toCharSequence());
				} else {
					result.append((String) part);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		append(s);
		return s.toString();
	}

	public String getEncoding() {
		Encoding e = placeHolder.getEncoding();
		if (e == null)
			return null;
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
		append(out);
		out.flush();
	}

	@Override
	public void render(PrintStream out) throws IOException {
		append(out);
		out.flush();
	}

	@Override
	public Set<String> names() {
		Set<String> result = new TreeSet<String>();
		for (Object part : parts) {
			if (part instanceof Location) {
				result.add(((Location) part).getName());
			}
		}
		return result;
	}

	@Override
	public Set<String> regionNames() {
		return children.keySet();
	}
	
	private CharSequence recentCS = null;
	private int csIndex = -1;
	private int recentStart = 0;
	@Override
	public char charAt(int index) {
		if (index < recentStart) {
			recentStart = 0;
			recentCS = null;
			csIndex = -1;
		}
		while ((recentCS == null || (index - recentStart) >= recentCS.length()) && csIndex + 1 < parts.length) {
			if (recentCS != null) {
				recentStart += recentCS.length();
			}
			csIndex++;
			recentCS = toCharSequence(csIndex);
		}
		return recentCS.charAt(index - recentStart);
	}

	private CharSequence toCharSequence(int index) {
		Object p = parts[index];
		return p instanceof Location ? ((Location)p).toCharSequence() : (String)p;
	}
	@Override
	public int length() {
		int l = 0;
		for (int i = 0; i < parts.length; i++) l += toCharSequence(i).length(); 
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
