package org.jproggy.snippetory.spi;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Set;

import org.jproggy.snippetory.Template;

public class TemplateWrapper implements Template {
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
		return wrapped.get(name);
	}

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
}
