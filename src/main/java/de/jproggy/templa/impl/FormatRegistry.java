package de.jproggy.templa.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.jproggy.templa.impl.Attributes.Types;
import de.jproggy.templa.spi.Format;
import de.jproggy.templa.spi.FormatFactory;

public class FormatRegistry {
	private Map<String, FormatFactory> formats = new HashMap<String, FormatFactory>();

	private FormatRegistry() {
	}

	public void register(String name, FormatFactory value) {
		Attributes.REGISTRY.register(name, Types.FORMAT);
		formats.put(name, value);
	}

	public Format get(String name, String definition) {
		FormatFactory f = formats.get(name);
		if (f == null) {
			return null;
		}
		return f.create(definition);
	}

	public Format get(String name, String definition, Locale l) {
		FormatFactory f = formats.get(name);
		if (f == null) {
			return null;
		}
		return f.create(definition, l);
	}

	public static final FormatRegistry INSTANCE = new FormatRegistry();
}
