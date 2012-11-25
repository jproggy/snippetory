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

import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.Transcoding;

class Metadata {
	public Metadata(String name, List<Format> formats, Encoding enc,
			 String fragment, String delimiter,
			String prefix, String suffix) {
		super();
		this.name = name;
		this.formats = formats.toArray(new Format[formats.size()]);
		this.enc = enc;
		this.fragment = fragment;
		this.delimiter = delimiter;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	final String name;
	private final Format[] formats;
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

	public Format[] getFormats() {
		return formats;
	}

}
