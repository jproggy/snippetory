package org.jproggy.snippetory.engine.spi;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatFactory;


public class NumFormat implements Format {
	private final NumberFormat impl;

	public NumFormat(String definition, Locale l) {
		impl = toFormat(definition, l);
	}
	
	private static NumberFormat toFormat(String definition, Locale l) {
		if ("".equals(definition)) return DecimalFormat.getNumberInstance(l);
		if ("currency".equals(definition)) return DecimalFormat.getCurrencyInstance(l);
		if ("int".equals(definition)) return DecimalFormat.getIntegerInstance(l);
		if ("percent".endsWith(definition)) return DecimalFormat.getPercentInstance(l);
		if ("JS".equals(definition)) return DecimalFormat.getNumberInstance(Locale.US);
		return new DecimalFormat(definition, DecimalFormatSymbols.getInstance(l));
	}

	@Override
	public CharSequence format(Object value) {
		return impl.format(value, new StringBuffer(), new FieldPosition(0));
	}

	@Override
	public boolean supports(Object value) {
		if (value instanceof Number) return true;
		return false;
	}
	
	public static class Factory implements FormatFactory {
		@Override
		public Format create(String definition, Locale l) {
			return new NumFormat(definition, l);
		}
	}
}
