package org.jproggy.snippetory.engine;

import java.util.Set;

public interface NamespaceContributor extends Cloneable {
	
	/**
	 * Sets all variables with given name to a String representation of the value.
	 * Exact value might differ according to different meta data associated with
	 * each of these variables. Eventually set or appended data is overwritten.
	 * All matching formats and encodings are used. However, there is some
	 * special handling for the interface (@link EncodedData). In this case the
	 * provided encoding in determined to calculate the correct transcoding.
	 * 
	 * @return the Template itself
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
	 * The names of all locations, to be accessed by the set operation. Can be used to ensure
	 * to access all existing names. This method belongs to the reflective API and as such
	 * is only for special use. <br />
	 * Use with care as it's a more expensive operation.
	 */
	Set<String> names();
	
	NamespaceContributor clone();
	
	void clear();
	
	CharSequence toCharSequence();
}