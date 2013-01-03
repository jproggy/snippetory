package org.jproggy.snippetory.engine;

import java.util.Set;

public interface DataSink {
	
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
	 * 
	 * @return the Template itself
	 */
	void append(String name, Object value);
	
	/**
	 * Declares the names supported by this DataSink. The returned Set must not
	 * change over time. It may return different instances, but it must be reliable
	 * to cache the result.
	 */
	Set<String> names();
	
	/**
	 * Create a new, clean instance. The returned instance has to be decoupled 
	 * in that status changes are not reflected on the called instance. Thus 
	 * immutable instance may return themselves.
	 */
	DataSink cleanCopy(Location parent);
	
	/**
	 * Reset to same state as directly after parsing the template.
	 */
	void clear();
	
	/**
	 * 
	 * @return
	 */
	CharSequence format();
}
