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
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatFactory;


public class StretchFormat implements Format {
	int length;
	Boolean left;

	public StretchFormat(String definition) {
		boolean num = true;
		for (char c : definition.toCharArray()) {
			if (num) {
				if (c >= '0' && c <= '9') {
					length = (10 * length) + (c - '0');
					continue;
				}
				num = false;				
			}
			if (c == 'l') { 
				if (left != null) throw new IllegalArgumentException("Alingment already defined");
				left = true;
			} else if (c == 'r') { 
				if (left != null) throw new IllegalArgumentException("Alingment already defined");
				left = false;
			}
		}
		if (length == 0) {
			throw new IllegalArgumentException("no length defined");
		}
		if (left == null) left = true;
	}

	@Override
	public String format(Object value) {
		String v = value.toString();
		if (v.length() >= length) {
			return v;
		}
		String b = blank(length - v.length());
		return left ? v + b : b + v;
	}
	
	private static String blanks = "                       ";

	private String blank(int i) {
		while (blanks.length() < i) {
			blanks += blanks;
		}
		return blanks.substring(0, i);
	}

	@Override
	public boolean supports(Object value) {
		if (value instanceof CharSequence) return true;
		return false;
	}
	
	public static class Factory implements FormatFactory {
		@Override
		public Format create(String definition, TemplateContext ctx) {
			return new StretchFormat(definition);
		}
	}
}
