package org.jproggy.snippetory.impl.spi;

import java.util.Locale;

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
		while (blanks.length() < i) blanks += blanks;
		return blanks.substring(0, i);
	}

	@Override
	public boolean supports(Object value) {
		if (value instanceof String) return true;
		return false;
	}
	
	public static class Factory implements FormatFactory {
		@Override
		public Format create(String definition, Locale l) {
			return new StretchFormat(definition);
		}
	}
}
