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

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class CaseFormatter implements FormatFactory {

	@Override
	public StringFormat create(String definition, TemplateContext ctx) {
		if ("upper".equals(definition)) return new Upper();
		if ("lower".equals(definition)) return new Lower();
		if ("firstUpper".equals(definition)) return new FirstUpper();
		if ("camelizeUpper".equals(definition)) return new Camelize(false);
		if ("camelizeLower".equals(definition)) return new Camelize(true);
		throw new IllegalArgumentException("defintion " + definition + " unknown.");
	}

	public static abstract class StringFormat extends SimpleFormat {
		@Override
		public boolean supports(Object value) {
			return CharDataSupport.isCharData(value);
		}
	}

	public static class Upper extends StringFormat {
		@Override
		public Object format(TemplateNode location, Object value) {
			return value.toString().toUpperCase();
		}
	}

	public static class Lower extends StringFormat {
		@Override
		public Object format(TemplateNode location, Object value) {
			return value.toString().toLowerCase();
		}
	}

	public static class FirstUpper extends StringFormat {
		@Override
		public Object format(TemplateNode location, Object value) {
			String s = value.toString();
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}
	}

	public static class Camelize extends StringFormat {
		public Camelize(boolean lower) {
			super();
			this.lower = lower;
		}
		private final boolean lower;
		@Override
		public Object format(TemplateNode location, Object value) {
			String s = value.toString();
			String[] vals = s.split("_|-");
			StringBuilder result = new StringBuilder();
			for (String val: vals) {
				if (val.length() == 0) continue;
				if (result.length() == 0 && lower) {
					result.append(val.substring(0, 1).toLowerCase());
				} else {
					result.append(val.substring(0, 1).toUpperCase());
				}
				if (val.length() > 1) result.append(val.substring(1).toLowerCase());
			}
			return result;
		}
	}
}
