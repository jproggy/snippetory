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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jproggy.snippetory.engine.chars.CharSequences;

public class DataSinks extends CharSequences implements DataSink {
	protected final DataSink[] parts;
	private final Location placeHolder;

	public DataSinks(List<DataSink> parts, Location placeHolder) {
		super();
		this.parts = parts.toArray(new DataSink[parts.size()]);
		this.placeHolder = placeHolder;
	}

	public DataSinks(DataSinks template, Location parent) {
		super();
		this.placeHolder = template.placeHolder.cleanCopy(parent);
		this.parts = new DataSink[template.parts.length];
		for (int i = 0; i < parts.length; i++) {
			this.parts[i] =  template.parts[i].cleanCopy(this.placeHolder);
		}
	}

	@Override
	public void set(String name, Object value) {
		for (DataSink v : parts) {
			v.set(name, value);
		}
	}

	@Override
	public void append(String name, Object value) {
		for (DataSink v : parts) {
			v.append(name, value);
		}
	}

	@Override
	public final  Set<String> names() {
		Set<String> result = new TreeSet<String>();
		for (DataSink part : parts) {
			result.addAll(part.names());
		}
		return result;
	}

	@Override
	public Set<String> regionNames() {
		Set<String> result = new TreeSet<String>();
		for (DataSink part : parts) {
			result.addAll(part.regionNames());
		}
		return result;
	}

	@Override
	public void clear() {
		for (DataSink v : parts) {
			v.clear();
		}
	}

	@Override
	protected int partCount() {
		return parts.length;
	}

	@Override
	protected CharSequence part(int index) {
		return parts[index].format();
	}

	@Override
	public <T extends Appendable> T appendTo(T to) {
		try {
			for (DataSink part : parts) {
			  CharSequences.append(to, part.format());
			}
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
		return to;
	}

	@Override
	public DataSinks cleanCopy(Location parent) {
		return new DataSinks(this, parent);
	}

	@Override
	public CharSequence format() {
		return this;
	}

	public Location getPlaceholder() {
		return placeHolder;
	}

	@Override
	public Region getChild(String name) {
		for (DataSink v : parts) {
			if (v.regionNames().contains(name)) return v.getChild(name);
		}
		return null;
	}
}
