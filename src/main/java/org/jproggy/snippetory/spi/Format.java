package org.jproggy.snippetory.spi;

import org.jproggy.snippetory.impl.FormatRegistry;


public interface Format {
	FormatRegistry REGISTRY = FormatRegistry.INSTANCE;
	
	String format(Object value);
	boolean supports(Object value);
}
