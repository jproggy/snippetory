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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatFactory;

public class DateFormater implements FormatFactory {
	private static final Map<String, Integer> LENGTHS = new TreeMap<String, Integer>(
			String.CASE_INSENSITIVE_ORDER);

	static {
		LENGTHS.put("short", DateFormat.SHORT);
		LENGTHS.put("medium", DateFormat.MEDIUM);
		LENGTHS.put("long", DateFormat.LONG);
		LENGTHS.put("full", DateFormat.FULL);
	}

	@Override
	public Format create(String definition, TemplateContext ctx) {
		return new DateFormatWrapper(toFormat(definition, ctx.getLocale()));
	}

	private DateFormat toFormat(String definition, Locale l) {
		if ("".equals(definition) && isTechLocale(l)) {
			definition = "sql_sql";
		}
		
		if ("".equals(definition))
			return DateFormat.getDateInstance(DateFormat.DEFAULT, l);

		// sql
		if ("sql".equals(definition))
			return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		if ("_sql".equals(definition))
			return new SimpleDateFormat("HH:mm:ss", Locale.US);
		if ("sql_sql".equals(definition))
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

		// JS
		if ("JS_NEW".equals(definition))
			return new SimpleDateFormat("'new Date('yyyy', 'MM', 'dd')'",
					Locale.US);
		if ("JS_NEW_FULL".equals(definition))
			return new SimpleDateFormat(
					"'new Date('yyyy', 'MM', 'dd', 'MM', 'dd', 'HH', 'mm', 'ss')'",
					Locale.US);

		return evaluateLengths(definition, l);
	}

	private boolean isTechLocale(Locale l) {
		return l.equals(TemplateContext.TECH);
	}
	
	private DateFormat evaluateLengths(String definition, Locale l) {
		// data by length
		Integer f = LENGTHS.get(definition);
		if (f != null) {
			return DateFormat.getDateInstance(f, l);
		}
		// time by length
		if (definition.charAt(0) == '_') {
			f = LENGTHS.get(definition.substring(1));
		}
		if (f != null) {
			return DateFormat.getTimeInstance(f, l);
		}
		// date time by length
		String[] both = definition.split("_");
		if (both.length == 2) {
			f = LENGTHS.get(both[0]);
			Integer t = LENGTHS.get(both[1]);
			if (f != null && t != null) {
				return DateFormat.getDateTimeInstance(f, t, l);
			}
		}
		return new SimpleDateFormat(definition, l);
	}

	public static class DateFormatWrapper implements Format {
		private final DateFormat impl;

		public DateFormatWrapper(DateFormat d) {
			impl = d;
		}

		@Override
		public CharSequence format(Object value) {
			if (value instanceof Calendar)
				return format(((Calendar) value).getTime());
			return impl.format(value, new StringBuffer(), new FieldPosition(0));
		}

		@Override
		public boolean supports(Object value) {
			if (value instanceof Date) return true;
			if (value instanceof Calendar) return true;
			return false;
		}
	}
}
