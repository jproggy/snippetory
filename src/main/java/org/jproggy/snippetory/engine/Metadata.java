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

import java.util.List;

import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.VoidFormat;

class Metadata {
	public Metadata(String name, List<Format> formats, Encoding enc,
			 String fragment, String delimiter,
			String prefix, String suffix, Metadata parent) {
		super();
		this.name = name;
		this.formats = formats.toArray(new Format[formats.size()]);
		this.enc = enc;
		this.fragment = fragment;
		this.delimiter = delimiter;
		this.prefix = prefix;
		this.suffix = suffix;
		this.parent = parent;
	}

	final String name;
	final Format[] formats;
	final Encoding enc;
	final String fragment;
	final String delimiter;
	final String prefix;
	final String suffix;
	final Metadata parent;

	Object format(Object value) {
		for (Format f : formats) {
			if (f.supports(value)) value = f.format(value);
		}
		return value;
	}

	Object toCharData(Object value) {
		if (isCharData(value)) return  value;
		for (Format f : formats) {
			if (f.supports(value)) {
				value = f.format(value);
				if (isCharData(value)) return  value;
			}
		}
		if (parent != null) return parent.toCharData(value);
		if (value == null) return "";
		return String.valueOf(value);
	}

	private boolean isCharData(Object value) {
		return CharDataSupport.isCharData(value);
	}

	Object formatVoid() {
		for (Format f : formats) {
			if (f instanceof VoidFormat) return ((VoidFormat)f).formatVoid();
		}
		return null;
	}

	public CharSequence getFallback() {
		if (prefix != null || suffix != null) return "";
		return fragment;
	}

}
