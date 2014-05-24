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
public final class EncodingRegistry {
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

	/**
	 * Resolve Encoding by name
	 * @return the registered encoding or null if none
	 */
	public Encoding get(String name) {
		return encodings.get(name);
	}

	public Collection<Transcoding> getOverwrites(Encoding target) {
		Collection<Transcoding> result = overwrites.get(target.getName());
		if (result == null) return Collections.emptyList();
		return result;
	}

	public static final EncodingRegistry INSTANCE = new EncodingRegistry();
}
