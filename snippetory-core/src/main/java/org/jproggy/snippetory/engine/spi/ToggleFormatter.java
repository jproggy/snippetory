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

package org.jproggy.snippetory.engine.spi;

import java.util.Collections;
import java.util.Set;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.chars.EncodedContainer;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.StateContainer;
import org.jproggy.snippetory.spi.TemplateNode;
import org.jproggy.snippetory.spi.VoidFormat;

public class ToggleFormatter implements FormatFactory {
	@Override
	public FormatConfiguration create(String definition, TemplateContext ctx) {
		return new Config(definition);
	}

	public static class ToggleFormat implements VoidFormat {
		private int count = 1;
		private final Config config;

		public ToggleFormat(Config definition) {
			super();
			config =  definition;
		}

		@Override
		public Object format(TemplateNode location, Object value) {
			try {
				if (value instanceof Number) {
					count = ((Number) value).intValue();
				}
				String data = config.values[Math.abs((count - 1) % config.values.length)];
        return new EncodedContainer(data, location.getEncoding());
			} finally {
				count++;
			}
		}

		@Override
		public boolean supports(Object value) {
			if (value == null) return true;
			return value instanceof Number;
		}

		@Override
		public Object formatVoid(TemplateNode node) {
			return format(null, node);
		}

		@Override
		public void clear(TemplateNode location) {
			count = 1;
		}

		@Override
		public void set(String name, Object value) {
		}

		@Override
		public void append(String name, Object value) {
		}

		@Override
		public Set<String> names() {
			return Collections.emptySet();
		}
	}

	private static class Config extends StateContainer<ToggleFormat> implements FormatConfiguration {
		private final String[] values;
		public Config(String definition) {
			super(KeyResolver.ROOT);
			this.values = definition.split(";");
		}

		@Override
		public ToggleFormat getFormat(TemplateNode node) {
			return get(node);
		}

		@Override
		protected ToggleFormat createValue(TemplateNode key) {
			return new ToggleFormat(this);
		}
	}
}
