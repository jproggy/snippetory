package org.jproggy.snippetory.engine;

import java.util.List;
import java.util.Set;

import org.jproggy.snippetory.spi.EncodedData;

public class ConditionalRegion extends DataSinks implements EncodedData {
	private final Set<String> names;
	private boolean appendMe;
	private final Location formatter;

	public ConditionalRegion(Location formatter, List<DataSink> parts) {
		super(parts);
		names = names();
		this.formatter = formatter;
	}
	
	protected ConditionalRegion(ConditionalRegion template) {
		super(template);
		formatter = template.formatter.clone();
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
	public ConditionalRegion clone() {
		return new ConditionalRegion(this);
	}

	@Override
	public CharSequence format() {
		if (!appendMe) return "";
		formatter.clear();
		formatter.append(this);
		return formatter.format();
	}
	
	@Override
	public CharSequence toCharSequence() {
		return this;
	}

	@Override
	public String getEncoding() {
		return formatter.getEncoding().getName();
	}

}
