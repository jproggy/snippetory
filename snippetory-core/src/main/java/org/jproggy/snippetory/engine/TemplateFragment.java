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

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.jproggy.snippetory.engine.chars.SelfAppender;

public class TemplateFragment implements DataSink, CharSequence, SelfAppender {
	private final CharSequence data;

	public TemplateFragment(CharSequence data2) {
		this.data = data2;
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

	@Override
	public Set<String> regionNames() {
		return Collections.emptySet();
	}

	@Override
	public Region getChild(String name) {
		return null;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	@Override
	public <T extends Appendable> T appendTo(T to) {
		try {
			to.append(data);
			return to;
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
	}

	@Override
	public int length() {
		return data.length();
	}

	@Override
	public char charAt(int index) {
		return data.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return data.subSequence(start, end);
	}

	public TemplateFragment start(int start) {
		return new TemplateFragment(this.subSequence(0,start));
	}

	public TemplateFragment end(int start) {
		return new TemplateFragment(this.subSequence(start, data.length()));
	}

	@Override
	public TemplateFragment cleanCopy(Location parent) {
		// cloning is not necessary. One instance is enough
		return this;
	}

	@Override
	public void clear() {
		// is immutable --> nothing to clear
	}

	@Override
	public CharSequence format() {
		return this;
	}
}
