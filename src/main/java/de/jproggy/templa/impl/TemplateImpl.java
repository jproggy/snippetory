package de.jproggy.templa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.jproggy.templa.Template;
import de.jproggy.templa.annotations.Encoded;
import de.jproggy.templa.spi.Encoding;

@Encoded
public class TemplateImpl implements Template, Cloneable {
	private List<Object> parts;
	private Map<String, Template> children;
	private final Variable placeHolder; 

	public TemplateImpl(Variable placeHolder, List<Object> parts, Map<String, Template> children) {
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
	public TemplateImpl set(String key, Object value) {
		for (Variable v: byName(key)) {
			v.set(value);
		}
		return this;
	}

	@Override
	public TemplateImpl append(String key, Object value) {
		for (Variable v: byName(key)) {
			v.append(value);
		}
		return this;
	}
	
	@Override
	public TemplateImpl clear() {
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
	
	@de.jproggy.templa.annotations.Encoding
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
}
