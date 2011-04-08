package de.jproggy.snippetory.spi;

import de.jproggy.snippetory.impl.FormatRegistry;


public interface Format {
	FormatRegistry REGISTRY = FormatRegistry.INSTANCE;
	
	String format(Object value);
	boolean supports(Object value);
}
