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
