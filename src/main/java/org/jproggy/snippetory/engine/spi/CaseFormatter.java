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
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class CaseFormatter implements FormatFactory {

	@Override
	public StringFormat create(String definition, TemplateContext ctx) {
		if ("upper".equals(definition)) return new Upper();
		if ("lower".equals(definition)) return new Lower();
		if ("firstUpper".equals(definition)) return new FirstUpper();
		if ("camelizeUpper".equals(definition)) return new Camelize(false);
		if ("camelizeLower".equals(definition)) return new Camelize(true);
		throw new IllegalArgumentException("defintion " + definition + " unknown.");
	}
	
	public static abstract class StringFormat extends SimpleFormat {
		@Override
		public boolean supports(Object value) {
			return value instanceof String;
		}
	}
	
	public static class Upper extends StringFormat {
		@Override
		public Object format(TemplateNode location, Object value) {
			return ((String)value).toUpperCase();
		}
	}
	
	public static class Lower extends StringFormat {
		@Override
		public Object format(TemplateNode location, Object value) {
			return ((String)value).toLowerCase();
		}
	}
	
	public static class FirstUpper extends StringFormat {
		@Override
		public Object format(TemplateNode location, Object value) {
			String s = (String)value;
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}
	}
	
	public static class Camelize extends StringFormat {
		public Camelize(boolean lower) {
			super();
			this.lower = lower;
		}
		private final boolean lower;
		@Override
		public Object format(TemplateNode location, Object value) {
			String s = (String)value;
			String[] vals = s.split("_|-");
			StringBuilder result = new StringBuilder();
			for (String val: vals) {
				if (result.length() == 0 && lower) {
					result.append(val.substring(0, 1).toLowerCase());
				} else {
					result.append(val.substring(0, 1).toUpperCase());
				}
				if (val.length() > 1) result.append(val.substring(1).toLowerCase());
			}
			return result;
		}
	}
}
