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

import java.util.Locale;

import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatFactory;

public class CaseFormater implements FormatFactory {

	@Override
	public Format create(String definition, Locale l) {
		if ("upper".equals(definition)) return new Upper();
		if ("lower".equals(definition)) return new Lower();
		if ("firstUpper".equals(definition)) return new FirstUpper();
		if ("camelizeUpper".equals(definition)) return new CamelizeUpper();
		if ("camelizeLower".equals(definition)) return new CamelizeLower();
		throw new IllegalArgumentException("defintion " + definition + " unknown.");
	}
	
	private static class Upper implements Format {
		@Override
		public CharSequence format(Object value) {
			return ((String)value).toUpperCase();
		}
		@Override
		public boolean supports(Object value) {
			return value instanceof String;
		}
	}
	
	private static class Lower implements Format {
		@Override
		public CharSequence format(Object value) {
			return ((String)value).toLowerCase();
		}
		@Override
		public boolean supports(Object value) {
			return value instanceof String;
		}
	}
	
	private static class FirstUpper implements Format {
		@Override
		public CharSequence format(Object value) {
			String s = (String)value;
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}
		@Override
		public boolean supports(Object value) {
			return value instanceof String;
		}
	}
	
	private static class CamelizeUpper implements Format {
		@Override
		public CharSequence format(Object value) {
			String s = (String)value;
			String[] vals = s.split("_|-");
			StringBuilder result = new StringBuilder();
			for (String val: vals) {
				result.append(val.substring(0, 1).toUpperCase());
				if (val.length() > 1) result.append(val.substring(1));
			}
			return result;
		}
		@Override
		public boolean supports(Object value) {
			return value instanceof String;
		}
	}
	
	private static class CamelizeLower implements Format {
		@Override
		public CharSequence format(Object value) {
			String s = (String)value;
			String[] vals = s.split("_|-");
			StringBuilder result = new StringBuilder();
			for (String val: vals) {
				if (result.length() == 0) {
					result.append(val.substring(0, 1).toLowerCase());
				} else {
					result.append(val.substring(0, 1).toUpperCase());
				}
				if (val.length() > 1) result.append(val.substring(1).toLowerCase());
			}
			return result;
		}
		@Override
		public boolean supports(Object value) {
			return value instanceof String;
		}
	}
}
