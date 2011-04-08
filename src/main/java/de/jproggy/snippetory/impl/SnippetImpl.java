package de.jproggy.snippetory.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.jproggy.snippetory.Snippetory;
import de.jproggy.snippetory.annotations.Encoded;
import de.jproggy.snippetory.spi.Encoding;

@Encoded
public class SnippetImpl implements Snippetory, Cloneable {
	private List<Object> parts;
	private Map<String, Snippetory> children;
	private final Variable placeHolder; 

	public SnippetImpl(Variable placeHolder, List<Object> parts, Map<String, Snippetory> children) {
		this.parts = parts;
		this.children = children;
		this.placeHolder = placeHolder;
	}

	@Override
	public Snippetory get(String... path) {
		if (path.length == 0) return this;
		Snippetory t = children.get(path[0]);
		for (int i = 1; i < path.length; i++) {
			t = t.get(path[i]);
		}
		return t;
	}
	
	private List<Variable> byName(String name) {
		List<Variable> result =  new ArrayList<Variable>();
		for (Object part: parts) {
			if (part instanceof Variable) {
				if (((Variable)part).getName().equals(name)) result.add((Variable)part);
			}
		}
		return result;
	}

	private List<Variable> variables() {
		List<Variable> result =  new ArrayList<Variable>();
		for (Object part: parts) {
			if (part instanceof Variable) {
				result.add((Variable)part);
			}
		}
		return result;
	}

	@Override
	public SnippetImpl set(String key, Object value) {
		for (Variable v: byName(key)) {
			v.set(value);
		}
		return this;
	}

	@Override
	public SnippetImpl append(String key, Object value) {
		for (Variable v: byName(key)) {
			v.append(value);
		}
		return this;
	}
	
	@Override
	public SnippetImpl clear() {
		for (Variable v : variables()) {
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
	
	@de.jproggy.snippetory.annotations.Encoding
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
	public void render(Snippetory target, String key) {
		target.append(key, this);
	}
	
	@Override
	public void render(Writer out) throws IOException {
		out.append(this.toString());
	}
	
	@Override
	public void render(PrintStream out) throws IOException {
		out.append(toString());
	}
}
