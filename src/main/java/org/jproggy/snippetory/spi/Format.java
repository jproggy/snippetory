package org.jproggy.snippetory.spi;

import org.jproggy.snippetory.engine.FormatRegistry;

/**
 * The format allows to encapsulate and reuse portions of view logic in a simple and generic
 * way. Even though not each and every view logic can be packed into a format it provides a
 * significant support. <br />
 * Formats a created by {@link FormatFactory FormatFactories}, that parse the definition 
 * found within the template file in order to create an appropriate {@code Format}. 
 * 
 * @author B. Ebertz
 */
public interface Format {
	/**
	 * Register {@link FormatFactory FormatFactories} here.
	 */
	FormatRegistry REGISTRY = FormatRegistry.INSTANCE;

	/**
	 * may only be called for supported values.
	 */
	CharSequence format(Object value);
	
	/**
	 * Many formats only apply to a special type or even specific
	 * values. The format method will only be called for supported values
	 */
	boolean supports(Object value);
}
