package org.jproggy.snippetory.spi;

import java.util.Set;


/**
 * <p>Extends the format to support the special case, that no value has been
 * provided to a location via @link Template.set or @link Template.append
 * method. (When rendering to a template append will be used internally.)
 * Just like the default format does. Other implementations can provide more
 * sophisticated algorithms to evaluate the rendered value.
 * </p>
 * <p> Be aware that there can be only one VoidFormat per location.
 * 
 * </p>
 * 
 * @author B. Ebertz
 * 
 */
public interface VoidFormat extends Format {

	Object formatVoid(TemplateNode node);

	/**
	 *  <p>
	 * Offers a value to the callee.  The callee is responsible for filtering out the 
	 * relevant names. Thus it's expected to ignore all names, that aren't listed in
	 * it's names(). However, the caller may do this filtering, too, as far as it provides
	 * a names declared by the names() method. The set method is intended to keep
	 * only a single value.
	 * </p>
	 */
	void set(String name, Object value);
	
	/**
	 * Appends a String representation of the value to all variables with given name.
	 * The exact value might differ according to different meta data associated with
	 * each of these variables. Eventually set or appended data is kept and new data 
	 * is appended behind the last character.
	 * All matching formats and encodings are used. However, there is some
	 * special handling for the interface (@link EncodedData). In this case the
	 * provided encoding in determined to calculate the correct transcoding.
	 */
	void append(String name, Object value);
	
	/**
	 * Declares the names supported by this Format. The returned Set must not
	 * change over time. It may return different instances, but it must be reliable
	 * to cache the result.
	 */
	Set<String> names();
}
