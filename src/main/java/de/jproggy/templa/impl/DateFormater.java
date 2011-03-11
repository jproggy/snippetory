package de.jproggy.templa.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.jproggy.templa.spi.Format;
import de.jproggy.templa.spi.FormatFactory;

public class DateFormater implements Format {
	private final DateFormat impl;

	public DateFormater(String definition, Locale l) {
		impl = toFormat(definition, l);
	}
	
	private static DateFormat toFormat(String definition, Locale l) {
		if ("".equals(definition)) return DateFormat.getDateInstance(DateFormat.DEFAULT, l) ;
		if ("short".equals(definition)) return DateFormat.getDateInstance(DateFormat.SHORT, l);
		if ("medium".endsWith(definition)) return DateFormat.getDateInstance(DateFormat.DEFAULT, l);
		if ("long".endsWith(definition)) return DateFormat.getDateInstance(DateFormat.LONG, l);
		if ("full".endsWith(definition)) return DateFormat.getDateInstance(DateFormat.FULL, l);
		if ("JS_NEW".equals(definition)) return new SimpleDateFormat("'new Date('yyyy', 'MM', 'dd')'", Locale.US);
		if ("JS_NEW_FULL".equals(definition)) return new SimpleDateFormat("'new Date('yyyy', 'MM', 'dd', 'MM', 'dd', 'HH', 'mm', 'ss')'", Locale.US);
		return new SimpleDateFormat(definition, l);
	}

	@Override
	public String format(Object value) {
		if (value instanceof Calendar) return format(((Calendar)value).getTime());
		return impl.format(value);
	}

	@Override
	public boolean supports(Object value) {
		if (value instanceof Date) return true;
		if (value instanceof Calendar) return true;
		return false;
	}
	
   static class Factory implements FormatFactory {
		@Override
		public Format create(String definition) {
			return null;
		}
		@Override
		public Format create(String definition, Locale l) {
			return new DateFormater(definition, l);
		}
	}
}
