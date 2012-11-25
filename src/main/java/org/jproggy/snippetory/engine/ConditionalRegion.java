package org.jproggy.snippetory.engine;

import java.util.List;
import java.util.Set;

import org.jproggy.snippetory.spi.EncodedData;

public class ConditionalRegion extends DataSinks implements EncodedData {
	private final Set<String> names;
	private boolean appendMe;

	public ConditionalRegion(Location formatter, List<DataSink> parts) {
		super(parts, formatter);
		names = names();
	}
	
	protected ConditionalRegion(ConditionalRegion template, Location parent) {
		super(template, template.getParent().cleanCopy(parent));
		names = names();
		appendMe =  false;
	}

	@Override
	public void set(String name, Object value) {
		if (names.contains(name)) {
			super.set(name, value);
			if (value != null) appendMe =  true;
		}
	}

	@Override
	public void append(String name, Object value) {
		if (names.contains(name)) {
			super.append(name, value);
			if (value != null) appendMe =  true;
		}
	}

	@Override
	public void clear() {
		super.clear();
		appendMe = false;
	}

	@Override
	public <T extends Appendable> T appendTo(T to) {
		return super.appendTo(to);
	}

	@Override
	public ConditionalRegion cleanCopy(Location parent) {
		return new ConditionalRegion(this, parent);
	}

	@Override
	public CharSequence format() {
		if (!appendMe) return "";
		getParent().clear();
		getParent().append(this);
		return getParent().format();
	}
	
	@Override
	public CharSequence toCharSequence() {
		return this;
	}

	@Override
	public String getEncoding() {
		return getParent().getEncoding().getName();
	}

}
