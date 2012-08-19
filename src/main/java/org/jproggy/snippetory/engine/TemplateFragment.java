package org.jproggy.snippetory.engine;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.jproggy.snippetory.engine.chars.SelfAppender;

public class TemplateFragment implements NamespaceContributor, CharSequence, SelfAppender {
	private final String data;
	
	public TemplateFragment(String data) {
		this.data = data;
	}

	@Override
	public void set(String name, Object value) {
	}

	@Override
	public void append(String name, Object value) {
	}

	@Override
	public Set<String> names() {
		return Collections.EMPTY_SET;
	}
	
	@Override
	public String toString() {
		return data;
	}

	@Override
	public <T extends Appendable> T appendTo(T to) {
		try {
			to.append(data);
			return to;
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
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
	
	public TemplateFragment start(int start) {
		return new TemplateFragment(data.substring(0,start));
	}
	
	public TemplateFragment end(int start) {
		return new TemplateFragment(data.substring(start));
	}

	@Override
	public TemplateFragment clone() {
		// cloning is not necessary. One instance is enough
		return this;
	}
	
	public void clear() {
		// is immutable --> nothing to clear
	}
	
	@Override
	public CharSequence toCharSequence() {
		return this;
	}
}
