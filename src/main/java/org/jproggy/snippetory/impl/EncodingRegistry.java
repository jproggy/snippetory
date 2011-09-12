package org.jproggy.snippetory.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Transcoding;

/**
 * Handles registration of encoding and transcoding overwrites.
 * See <a href="http://www.jproggy.org/snippetory/Encodings.html">offical documentation</a>
 * for additional information
 * @author B. Ebertz
 */
public class EncodingRegistry {
	private Map<String, Encoding> encodings = new HashMap<String, Encoding>();
	private Map<String, Collection<Transcoding>> overwrites = new HashMap<String, Collection<Transcoding>>();

	private EncodingRegistry() {
	}

	public void register(Encoding value) {
		encodings.put(value.getName(), value);
	}

	public void registerOverwite(Encoding target, Transcoding overwrite) {
		Collection<Transcoding> values = overwrites.get(target.getName());
		if (values == null) {
			values = new ArrayList<Transcoding>();
			overwrites.put(target.getName(), values);
		}
		values.add(overwrite);
	}

	public Encoding get(String name) {
		return encodings.get(name);
	}

	@SuppressWarnings("unchecked")
	public Collection<Transcoding> getOverwrites(Encoding target) {
		Collection<Transcoding> result = overwrites.get(target.getName());
		if (result == null) return Collections.EMPTY_LIST;
		return result;
	}

	public static final EncodingRegistry INSTANCE = new EncodingRegistry();
}
