package org.jproggy.snippetory.engine;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.annotations.Encoded;
import org.jproggy.snippetory.spi.Encoding;


@Encoded
public class Region implements Template, Cloneable {
	private List<Object> parts;
	private Map<String, Template> children;
	private final Location placeHolder; 

	public Region(Location placeHolder, List<Object> parts, Map<String, Template> children) {
		this.parts = parts;
		this.children = children;
		this.placeHolder = placeHolder;
	}

	@Override
	public Template get(String... path) {
		if (path.length == 0) return this;
		Template t = children.get(path[0]);
		for (int i = 1; i < path.length; i++) {
			t = t.get(path[i]);
		}
		return t;
	}
	
	private List<Location> byName(String name) {
		List<Location> result =  new ArrayList<Location>();
		for (Object part: parts) {
			if (part instanceof Location) {
				if (((Location)part).getName().equals(name)) result.add((Location)part);
			}
		}
		return result;
	}

	private List<Location> locations() {
		List<Location> result =  new ArrayList<Location>();
		for (Object part: parts) {
			if (part instanceof Location) {
				result.add((Location)part);
			}
		}
		return result;
	}

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
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Object part : parts) {
			result.append(part.toString());
		}
		return result.toString();
	}
	
	@org.jproggy.snippetory.annotations.Encoding
	public String getEncoding() {
		Encoding e = placeHolder.getEncoding();
		if (e == null) return null;
		return e.getName();
	}

	@Override
	public void render() {
		placeHolder.append(this);
		clear();
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
}
