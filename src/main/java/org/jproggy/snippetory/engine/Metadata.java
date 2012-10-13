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

import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;

class Metadata {
	public Metadata(String name, List<Format> formats, Encoding enc,
			String defaultVal, String fragment, String delimiter,
			String prefix, String suffix, Metadata parent) {
		super();
		this.name = name;
		this.formats = formats.toArray(new Format[formats.size()]);
		this.enc = enc;
		this.defaultVal = defaultVal;
		this.fragment = fragment;
		this.delimiter = delimiter;
		this.prefix = prefix;
		this.suffix = suffix;
		this.parent = parent;
	}

	final String name;
	final Format[] formats;
	final Encoding enc;
	final String defaultVal;
	final String fragment;
	final String delimiter;
	final String prefix;
	final String suffix;
	final Metadata parent;

	CharSequence format(CharSequence value) {
		for (Format f : formats) {
			if (f.supports(value)) value = f.format(value);
		}
		return value;
	}

	CharSequence toString(Object value) {
		if (value instanceof CharSequence) {
			return (CharSequence) value;
		}
		for (Format f : formats) {
			if (f.supports(value)) return f.format(value);
		}
		if (parent != null) return parent.toString(value);
		if (value == null) return "";
		return String.valueOf(value);
	}

}
