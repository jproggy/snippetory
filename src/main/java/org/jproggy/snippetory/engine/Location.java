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
import org.jproggy.snippetory.spi.Transcoding;

public class Location implements DataSink, Cloneable {
	final Metadata md;
	private StringBuilder target;

	public Location(Location parent, String name, Map<String, String> attribs,
			String fragment, TemplateContext ctx) {
		List<Format> formats = new ArrayList<Format>();
		String delimiter = null;
		String prefix = null;
		String suffix = null;
		Encoding enc = parent == null ? Encodings.NULL : parent.getEncoding();
		for (String attr : attribs.keySet()) {
			switch (Attributes.REGISTRY.type(attr)) {
			case FORMAT:
				formats.add(FormatRegistry.INSTANCE.get(attr,
						attribs.get(attr), ctx));
				break;
			case ENCODING:
				enc = EncodingRegistry.INSTANCE.get(attribs.get(attr));
				break;
			case DELIMITER:
				delimiter = attribs.get(attr);
				break;
			case PREFIX:
				prefix = attribs.get(attr);
				break;
			case SUFFIX:
				suffix = attribs.get(attr);
				break;
			default:
				throw new SnippetoryException("Attribute " + attr + " has unknown type " + Attributes.REGISTRY.type(attr));
			}
		}
		md = new Metadata(name, formats, enc, fragment, delimiter, prefix, suffix, metadata(parent));
	}

	private static Metadata metadata(Location parent) {
		return parent == null ? null : parent.md;
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
		Object f = md.formatVoid();
		if (f == null) {
			return md.getFallback();
		}
		if (f instanceof EncodedData) {
			EncodedData data = (EncodedData)f;
			if (getEncoding().getName().equals(data.getEncoding())) {
				return data.toCharSequence();
			}
			return transcode(new StringBuilder(), data.toCharSequence(), data.getEncoding());
		}
		return f.toString();
	}

	private void set(Object value) {
		clear();
		append(value);
	}

	protected void append(Object value) {
		prepareTarget();
		Object formatted = md.format(md.toCharData(value));
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
			transcode(target, CharDataSupport.toCharSequence(formated), sourceEnc);
		}
	}

	private String getEncoding(Object value, Object formatted) {
		if (formatted instanceof EncodedData) return ((EncodedData)formatted).getEncoding();
		return CharDataSupport.getEncoding(value);
	}

	private <T extends Appendable> T transcode(T target, CharSequence value, String sourceEnc) {
		Encoding targetEnc =  getEncoding();
		try {
			for (Transcoding overwrite : EncodingRegistry.INSTANCE.getOverwrites(targetEnc)) {
				if (overwrite.supports(sourceEnc, targetEnc.getName())) {
					overwrite.transcode(target, value, sourceEnc, targetEnc.getName());
					return target;
				}
			}
			targetEnc.transcode(target, value, sourceEnc);
			return target;
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
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
	public Location cleanCopy() {
		try {
			Location clone = (Location)super.clone();
			clone.clear();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new SnippetoryException(e);
		}
	}
}
