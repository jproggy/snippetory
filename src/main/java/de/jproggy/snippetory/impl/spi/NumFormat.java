package de.jproggy.snippetory.impl.spi;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import de.jproggy.snippetory.spi.Format;
import de.jproggy.snippetory.spi.FormatFactory;

public class NumFormat implements Format {
	private final NumberFormat impl;

	public NumFormat(String definition, Locale l) {
		impl = toFormat(definition, l);
	}
	
	private static NumberFormat toFormat(String definition, Locale l) {
		if ("currency".equals(definition)) return DecimalFormat.getCurrencyInstance(l);
		if ("".equals(definition)) return DecimalFormat.getNumberInstance(l);
		if ("percent".endsWith(definition)) return DecimalFormat.getPercentInstance(l);
		if ("JS".equals(definition)) return DecimalFormat.getNumberInstance(Locale.US);
		return new DecimalFormat(definition, DecimalFormatSymbols.getInstance(l));
	}

	@Override
	public String format(Object value) {
		String f = impl.format(value);
		f.replaceAll(" ", "\u0160"); // ensure number are shown without break
		return f;
	}

	@Override
	public boolean supports(Object value) {
		if (value instanceof Number) return true;
		return false;
	}
	
	public static class Factory implements FormatFactory {
		@Override
		public Format create(String definition) {
			return null;
		}
		@Override
		public Format create(String definition, Locale l) {
			return new NumFormat(definition, l);
		}
	}
}
