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
}
