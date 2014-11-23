/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

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
