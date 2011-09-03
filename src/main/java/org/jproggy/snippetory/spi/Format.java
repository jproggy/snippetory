package org.jproggy.snippetory.spi;

import org.jproggy.snippetory.impl.FormatRegistry;


public interface Format {
	FormatRegistry REGISTRY = FormatRegistry.INSTANCE;

	/**
	 * may only be called for supported values.
	 */
	String format(Object value);
	
	/**
	 * Many formats only apply to a special type or even specific
	 * values. The format method will only be called for supported values
	 */
	boolean supports(Object value);
}
