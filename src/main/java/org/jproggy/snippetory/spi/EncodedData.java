package org.jproggy.snippetory.spi;


public interface EncodedData {
	String getEncoding();
	
	/**
	 * This methods is added, because a toString method might be more expensive
	 * in many cases. This method allows to return a StringBuilder instead of a String.
	 * This might avoid copying the data from the StringBuilder into a String. 
	 */
	CharSequence toCharSequence();
}
