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
import org.jproggy.snippetory.engine.spi.PadFormatter.Alignment;
import org.jproggy.snippetory.engine.spi.PadFormatter.PadFormat;
import org.jproggy.snippetory.spi.FormatFactory;

public class StretchFormatter implements FormatFactory {
	@Override
	public PadFormat create(String definition, TemplateContext ctx) {
		PadFormat f = null;
		int length = 0;
		Alignment left = null;
		for (char c : definition.toCharArray()) {
			if (f == null) {
				if (c >= '0' && c <= '9') {
					length = (10 * length) + (c - '0');
					continue;
				}
				f = new PadFormat(length);
			}
			if (c == 'l') {
				if (left != null) throw new IllegalArgumentException("Alingment already defined");
				left = Alignment.left;
			} else if (c == 'r') {
				if (left != null) throw new IllegalArgumentException("Alingment already defined");
				left = Alignment.right;
			}
		}
		if (length == 0) {
			throw new IllegalArgumentException("no length defined");
		} else if (f == null) {
			f = new PadFormat(length);
		}
		if (left != null) f.setAlign(left);
		return f;
	}
}
