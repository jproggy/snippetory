/*******************************************************************************
 * Copyright (c) 2011-2012 JProggy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR 
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE, 
 * NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE
 *******************************************************************************/

package org.jproggy.snippetory.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.engine.spi.CaseFormater;
import org.jproggy.snippetory.engine.spi.DateFormater;
import org.jproggy.snippetory.engine.spi.NumFormater;
import org.jproggy.snippetory.engine.spi.ShortenFormat;
import org.jproggy.snippetory.engine.spi.StretchFormat;
import org.jproggy.snippetory.engine.spi.ToggleFormat;
import org.jproggy.snippetory.spi.Configurer;


class Attributes {
	static final String BACKWARD = "backward";
	public static class Registry {
		private final Map<String, Types> attribs = new HashMap<String, Types>();
		private Registry() {}
		public void register(String name, Types value) {
			Types old = attribs.get(name);
			if (old != null && old.equals(value)) {
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
		REGISTRY.register(BACKWARD, Types.BACKWARD);
		FormatRegistry.INSTANCE.register("stretch", new StretchFormat.Factory());
		FormatRegistry.INSTANCE.register("shorten", new ShortenFormat.Factory());
		FormatRegistry.INSTANCE.register("number", new NumFormater());
		FormatRegistry.INSTANCE.register("date", new DateFormater());
		FormatRegistry.INSTANCE.register("toggle", new ToggleFormat.Factory());
		FormatRegistry.INSTANCE.register("case", new CaseFormater());
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
		FORMAT, DEFAULT, ENCODING, DELIMITER, PREFIX, SUFFIX, BACKWARD
	}
}
