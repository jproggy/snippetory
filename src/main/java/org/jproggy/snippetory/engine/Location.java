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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.Transcoding;

public class Location {
	final Metadata md;
	private StringBuilder target;

	Location(Location template) {
		this.md = template.md;
	}

	public Location(Location parent, String name, Map<String, String> attribs,
			String fragment, Locale l) {
		List<Format> formats = new ArrayList<Format>();
		String defaultVal = null;
		String delimiter = null;
		String prefix = null;
		String suffix = null;
		Encoding enc = parent == null ? Encodings.NULL : parent.getEncoding();
		for (String attr : attribs.keySet()) {
			switch (Attributes.REGISTRY.type(attr)) {
			case FORMAT:
				formats.add(FormatRegistry.INSTANCE.get(attr,
						attribs.get(attr), l));
				break;
			case DEFAULT:
				defaultVal = attribs.get(attr);
				break;
			case ENCODING:
				enc = EncodingRegistry.INSTANCE.get(attribs.get(attr));
				break;
			case DELIMITER:
				delimiter = attribs.get(attr);
				break;
			case PREFIX:
				prefix = attribs.get(attr);
				if (defaultVal == null)
					defaultVal = "";
				break;
			case SUFFIX:
				suffix = attribs.get(attr);
				if (defaultVal == null)
					defaultVal = "";
				break;
			default:
				// ignore no practical meaning
				break;
			}
		}
		md = new Metadata(name, formats, enc, defaultVal, fragment, delimiter,
				prefix, suffix, parent == null ? null : parent.md);
	}

	@Override
	public String toString() {
		return toCharSequence().toString();
	}

	public CharSequence toCharSequence() {
		if (target != null) {
			if (md.suffix != null)
				return target.toString() + md.suffix;
			return target;
		}
		CharSequence f;
		f = md.format(md.defaultVal);
		if (md.defaultVal == null && f != null) {
			try {
				StringBuilder r = new StringBuilder();
				getEncoding().escape(r, f);
				return r;
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}
		if (f != null) {
			return f;
		}
		return md.fragment;
	}

	public void set(Object value) {
		clear();
		append(value);
	}

	public void append(Object value) {
		try {
			if (target == null) {
				target = md.prefix == null ? new StringBuilder()
						: new StringBuilder(md.prefix);
			} else {
				if (md.delimiter != null)
					target.append(md.delimiter);
			}
			if (value instanceof EncodedData) {
				handleEncodedData((EncodedData) value);
			} else {
				getEncoding().escape(target, md.format(md.toString(value)));
			}
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
	}

	private void handleEncodedData(EncodedData value) throws IOException {
		String sourceEnc = value.getEncoding();

		// normalize empty to null-encoding
		if (sourceEnc == null || sourceEnc.length() == 0) {
			sourceEnc = Encodings.NULL.getName();
		}
		Encoding myEnc = getEncoding();
		if (sourceEnc.equals(myEnc.getName())) {
			CharSequence formated = md.format(value.toCharSequence());
			if (formated instanceof Region) {
				((Region) formated).appendTo(target);
			} else {
				target.append(formated);
			}
		} else {
			transcode(value, sourceEnc, myEnc);
		}
	}

	private void transcode(EncodedData value, String sourceEnc,
			Encoding targetEnc) throws IOException {
		for (Transcoding overwrite : EncodingRegistry.INSTANCE
				.getOverwrites(targetEnc)) {
			if (overwrite.supports(sourceEnc, targetEnc.getName())) {
				overwrite.transcode(target, md.format(value.toString()),
						sourceEnc, targetEnc.getName());
				return;
			}
		}
		targetEnc.transcode(target, md.format(value.toString()), sourceEnc);
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
}
