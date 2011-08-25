package org.jproggy.snippetory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.impl.spi.DateFormater;
import org.jproggy.snippetory.impl.spi.NumFormat;
import org.jproggy.snippetory.impl.spi.ShortenFormat;
import org.jproggy.snippetory.impl.spi.StretchFormat;
import org.jproggy.snippetory.spi.Configurer;


class Attributes {
	public static class Registry {
		private Map<String, Types> attribs = new HashMap<String, Types>();
		private Registry() {}
		public void register(String name, Types value) {
			Types old = attribs.get(name);
			if (old != null && old != value) {
				throw new SnippetoryException("attribute " + name + " alreadeay defined oterhwise.");
			}
			attribs.put(name, value);
		}
		public Types type(String name) {
			return attribs.get(name);
		}
		public List<String> names(Types type) {
			List<String> names = new ArrayList<String>();
			for (Map.Entry<String, Types> e: attribs.entrySet()) {
				if (e.getValue() == type) names.add(e.getKey());
			}
			return names;
		}
	}
	static final Registry REGISTRY = new Registry();
	static {
		REGISTRY.register("default", Types.DEFAULT);
		REGISTRY.register("enc", Types.ENCODING);
		REGISTRY.register("delimiter", Types.DELIMITER);
		REGISTRY.register("prefix", Types.PREFIX);
		REGISTRY.register("suffix", Types.SUFFIX);
		FormatRegistry.INSTANCE.register("stretch", new StretchFormat.Factory());
		FormatRegistry.INSTANCE.register("shorten", new ShortenFormat.Factory());
		FormatRegistry.INSTANCE.register("number", new NumFormat.Factory());
		FormatRegistry.INSTANCE.register("date", new DateFormater.Factory());
		for (Encodings e: Encodings.values()) {
			EncodingRegistry.INSTANCE.register(e);
		}
		for (Configurer c: ServiceLoader.load(Configurer.class)) {
			// avoid optimize this loop, as iterating is necessary to load the classes
			// i.e. to initialize the extensions
			c.getClass();
		}
	}
	enum Types {
		FORMAT, DEFAULT, ENCODING, DELIMITER, PREFIX, SUFFIX
	}
}
