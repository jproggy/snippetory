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

package org.jproggy.snippetory.engine.spi;
 
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatFactory;



public class ShortenFormat implements Format {
	int length;
	String suffix = "";

	public ShortenFormat(String definition) {
		boolean num = true;
		for (char c : definition.toCharArray()) {
			if (num) {
				if (c >= '0' && c <= '9') {
					length = (10 * length) + (c - '0');
					continue;
				}
				num = false;				
			}
			suffix += c;
		}
		if (length == 0) {
			throw new IllegalArgumentException("no length defined");
		}
		if (length < suffix.length()) {
			throw new IllegalArgumentException("Suffix too long");
		}
	}


	@Override
	public CharSequence format(Object value) {
		CharSequence s = CharDataSupport.toCharSequence(value);
		if (s.length() <= length) return s; 
		return new StringBuilder(s.subSequence(0, length - suffix.length())).append(suffix);
	}


	@Override
	public boolean supports(Object value) {
		return CharDataSupport.isCharData(value);
	}
	
	public static class Factory implements FormatFactory {
		@Override
		public Format create(String definition, TemplateContext ctx) {
			return new ShortenFormat(definition);
		}
	}
}
