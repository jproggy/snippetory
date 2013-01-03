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
	
	private ConditionalRegion(ConditionalRegion template, Location parent) {
		super(template, template.getPlaceholder().cleanCopy(parent));
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
		getPlaceholder().clear();
		appendMe = false;
	}

	@Override
	public ConditionalRegion cleanCopy(Location parent) {
		return new ConditionalRegion(this, parent);
	}

	@Override
	public CharSequence format() {
		Location placeholder = getPlaceholder();
		if (appendMe) {
			placeholder.set(this);
		}
		return placeholder.format();
	}
	
	@Override
	public CharSequence toCharSequence() {
		return this;
	}

	@Override
	public String getEncoding() {
		return getPlaceholder().getEncoding();
	}

}
