package org.jproggy.snippetory.engine;

import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;

class Metadata {
	public Metadata(String name, Format[] formats, Encoding enc,
			String defaultVal, String fragment, String delimiter,
			String prefix, String suffix, Metadata parent) {
		super();
		this.name = name;
		this.formats = formats;
		this.enc = enc;
		this.defaultVal = defaultVal;
		this.fragment = fragment;
		this.delimiter = delimiter;
		this.prefix = prefix;
		this.suffix = suffix;
		this.parent = parent;
	}
	final String name;
	final Format[] formats;
	final Encoding enc;
	final String defaultVal;
	final String fragment;
	final String delimiter;
	final String prefix;
	final String suffix;
	final Metadata parent;
	
	CharSequence format(CharSequence value) {
		for (Format f: formats) {
			if (f.supports(value)) value = f.format(value);
		}
		return value;
	}
	CharSequence toString(Object value) {
		if (value instanceof CharSequence) {
			return (String) value;
		}
		for (Format f: formats) {
			if (f.supports(value)) return f.format(value);
		}
		if (parent != null) return parent.toString(value);
		if (value == null) return "";
		return String.valueOf(value);
	}

}
