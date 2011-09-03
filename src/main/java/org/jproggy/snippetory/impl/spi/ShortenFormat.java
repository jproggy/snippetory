package org.jproggy.snippetory.impl.spi;

import java.util.Locale;

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
	}


	@Override
	public String format(Object value) {
		String s = (String)value;
		if (s.length() <= length) return s; 
		return s.substring(0, length - suffix.length()) + suffix;
	}


	@Override
	public boolean supports(Object value) {
		if (value instanceof String) return true;
		return false;
	}
	
	public static class Factory implements FormatFactory {
		@Override
		public Format create(String definition, Locale l) {
			return new ShortenFormat(definition);
		}
	}
}
