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

import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.spi.TemplateNode;
import org.jproggy.snippetory.spi.Transcoding;
import org.jproggy.snippetory.spi.VoidFormat;

class Metadata implements VoidFormat {
	
	public Metadata(String name, String fragment, Attributes attribs) {
		super();
		this.name = name;
		this.formats = attribs.formats.values().toArray(new FormatConfiguration[attribs.formats.size()]);
		this.enc = attribs.enc;
		this.fragment = fragment;
		this.delimiter = attribs.delimiter;
		this.prefix = attribs.prefix;
		this.suffix = attribs.suffix;
	}

	final String name;
	final FormatConfiguration[] formats;
	final Encoding enc;
	final String fragment;
	final String delimiter;
	final String prefix;
	final String suffix;

	public CharSequence getFallback() {
		if (prefix != null || suffix != null) return "";
		return fragment;
	}

	public <T extends Appendable> T transcode(T target, CharSequence value, String sourceEnc) {
		try {
			for (Transcoding overwrite : EncodingRegistry.INSTANCE.getOverwrites(enc)) {
				if (overwrite.supports(sourceEnc, enc.getName())) {
					overwrite.transcode(target, value, sourceEnc, enc.getName());
					return target;
				}
			}
			enc.transcode(target, value, sourceEnc);
			return target;
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
	}
	
	Format[] getFormats(TemplateNode location) {
		Format[] result = new Format[formats.length];
		for (int i = 0; i < formats.length; i++) {
			result[i] = formats[i].getFormat(location);
		}
		return result;
	}

	@Override
	public Object format(TemplateNode location, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supports(Object value) {
		return false;
	}

	@Override
	public void clear(TemplateNode location) {
	}

	@Override
	public Object formatVoid(TemplateNode node) {
		return getFallback();
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
}
