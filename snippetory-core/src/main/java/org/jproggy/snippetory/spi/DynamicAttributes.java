package org.jproggy.snippetory.spi;

/**
 * The interface can be implemented by {@link FormatConfiguration FormatConfigurations} that
 * do not know their exact attributes at compile time.
 *
 * @author B. Ebertz
 *
 */
public interface DynamicAttributes {
	/**
	 * Attributes,that are defined in template code but not defined as setter methods,
	 * will be provided over this method.
	 * <p>
	 * Example:<br />
	 * <pre>
	 *   $(button='Test' button.id='btn_id' button.onclick='buttonClicked()')
	 * </pre>
	 * If there is a setter method <code>setId(String val)</code> this will be called
	 * with parameter <code>"btn_id"</code> and <code>setAttribute</code> will
	 * be called with <code>("onclick", "buttonClicked()")</code>.
	 */
	void setAttribute(String name, String value);
}
