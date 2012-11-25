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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.chars.SelfAppender;
import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.VoidFormat;

public class Location implements DataSink, Cloneable {
	final Metadata md;
	private StringBuilder target;
	private final Format[] formats;
	private final Location parent;

	public Location(Location parent, String name, Map<String, String> attribs,
			String fragment, TemplateContext ctx) {
		this.parent = parent;
		List<Format> formats = new ArrayList<Format>();
		String delimiter = null;
		String prefix = null;
		String suffix = null;
		Encoding enc = parent == null ? Encodings.NULL : parent.getEncoding();
		for (Map.Entry<String, String> attr : attribs.entrySet()) {
			switch (Attributes.REGISTRY.type(attr.getKey())) {
			case FORMAT:
				formats.add(FormatRegistry.INSTANCE.get(attr.getKey(),
						attr.getValue(), ctx));
				break;
			case ENCODING:
				enc = EncodingRegistry.INSTANCE.get(attr.getValue());
				break;
			case DELIMITER:
				delimiter = attr.getValue();
				break;
			case PREFIX:
				prefix = attr.getValue();
				break;
			case SUFFIX:
				suffix = attr.getValue();
				break;
			default:
				throw new SnippetoryException("Attribute " + attr.getKey() + " has unknown type " + Attributes.REGISTRY.type(attr.getKey()));
			}
		}
		md = new Metadata(name, formats, enc, fragment, delimiter, prefix, suffix);
		this.formats = md.getFormats();
	}

	@Override
	public String toString() {
		return format().toString();
	}

	public CharSequence format() {
		if (target != null) {
			if (md.suffix != null) return target.toString() + md.suffix;
			return target;
		}
		Object f = formatVoid();
		if (f instanceof EncodedData) {
			EncodedData data = (EncodedData)f;
			if (getEncoding().getName().equals(data.getEncoding())) {
				return data.toCharSequence();
			}
			return md.transcode(new StringBuilder(), data.toCharSequence(), data.getEncoding());
		}
		return f.toString();
	}

	private void set(Object value) {
		clear();
		append(value);
	}

	protected void append(Object value) {
		prepareTarget();
		Object formatted = format(toCharData(value));
		String sourceEncoding = getEncoding(value, formatted);
		writeToTarget(formatted, sourceEncoding);
	}

	private void prepareTarget() {
		if (target == null) {
			target = md.prefix == null ? new StringBuilder() : new StringBuilder(md.prefix);
		} else {
			if (md.delimiter != null) target.append(md.delimiter);
		}
	}

	private void writeToTarget(Object formated, String sourceEnc) {
		if (sourceEnc.equals(getEncoding().getName())) {
			if (formated instanceof SelfAppender) {
				((SelfAppender) formated).appendTo(target);
			} else {
				target.append(formated);
			}
		} else {
			md.transcode(target, CharDataSupport.toCharSequence(formated), sourceEnc);
		}
	}

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
		return md.getFallback();
	}

	private String getEncoding(Object value, Object formatted) {
		if (formatted instanceof EncodedData) return ((EncodedData)formatted).getEncoding();
		return CharDataSupport.getEncoding(value);
	}

	public void clear() {
		target = null;
	}

	public String getName() {
		return md.name;
	}

	public Encoding getEncoding() {
		return md.enc;
	}

	@Override
	public void set(String name, Object value) {
		if (name.equals(md.name)) set(value);
		
	}

	@Override
	public void append(String name, Object value) {
		if (name.equals(md.name)) append(value);		
	}

	@Override
	public Set<String> names() {
		return Collections.singleton(md.name);
	}

	@Override
	public Location cleanCopy(Location parent) {
		try {
			Location clone = (Location)super.clone();
			clone.clear();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new SnippetoryException(e);
		}
	}
}
