package de.jproggy.snippetory.impl;

import java.util.HashMap;
import java.util.Map;

import de.jproggy.snippetory.impl.Attributes.Types;
import de.jproggy.snippetory.spi.Encoding;

public class EncodingRegistry {
	private Map<String, Encoding> encodings = new HashMap<String, Encoding>();

	private EncodingRegistry() {
	}

	public void register(Encoding value) {
		Attributes.REGISTRY.register(value.getName(), Types.FORMAT);
		encodings.put(value.getName(), value);
	}

	public Encoding get(String name) {
		return encodings.get(name);
	}

	public static final EncodingRegistry INSTANCE = new EncodingRegistry();
}
