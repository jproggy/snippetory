package org.jproggy.snippetory.engine;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jproggy.snippetory.engine.chars.CharSequences;
import org.jproggy.snippetory.engine.chars.SelfAppender;

public class DataSinks extends CharSequences implements DataSink, SelfAppender {
	private final DataSink[] parts;

	public DataSinks(List<DataSink> parts) {
		super();
		this.parts = parts.toArray(new DataSink[parts.size()]);
	}

	protected DataSinks(DataSinks template) {
		super();
		this.parts = new DataSink[template.parts.length];
		for (int i = 0; i < parts.length; i++) {
			this.parts[i] =  template.parts[i].clone();
		}
	}

	@Override
	public void set(String name, Object value) {
		for (DataSink v : parts) {
			v.set(name, value);
		}
	}

	@Override
	public void append(String name, Object value) {
		for (DataSink v : parts) {
			v.append(name, value);
		}
	}

	@Override
	public final  Set<String> names() {
		Set<String> result = new TreeSet<String>();
		for (DataSink part : parts) {
			result.addAll(part.names());
		}
		return result;
	}

	@Override
	public void clear() {
		for (DataSink v : parts) {
			v.clear();
		}
	}

	@Override
	protected int partCount() {
		return parts.length;
	}

	@Override
	protected CharSequence part(int index) {
		return parts[index].format();
	}

	@Override
	public <T extends Appendable> T appendTo(T to) {
		try {
			for (DataSink part : parts) {
				to.append(part.format());
			}
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
		return to;
	}
	
	@Override
	public DataSinks clone() {
		return new DataSinks(this);
	}

	@Override
	public CharSequence format() {
		return this;
	}
}
