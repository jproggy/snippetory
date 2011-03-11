package de.jproggy.templa.spi;

import de.jproggy.templa.impl.FormatRegistry;


public interface Format {
	FormatRegistry REGISTRY = FormatRegistry.INSTANCE;
	
	String format(Object value);
	boolean supports(Object value);
}
