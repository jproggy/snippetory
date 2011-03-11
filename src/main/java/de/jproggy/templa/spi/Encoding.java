package de.jproggy.templa.spi;

import de.jproggy.templa.impl.IncompatibleEncodingException;

/**
 * The purpose of an encoding is to ensure the syntactical correctness of an output by 
 * escaping terms or characters with special meaning in a way that   
 */
public interface Encoding {
	/**
	 * will escape the content of val appropriate to the supported output format and append
	 * the result to target.
	 */
	void encode(StringBuilder target, String val);
	/**
	 * 
	 * @param target
	 * @param value
	 * @param encodingName
	 * @throws IncompatibleEncodingException if the encoding can't be taken as is and can't
	 * be decoded.
	 */
	void transcode(StringBuilder target, String value, String encodingName) throws IncompatibleEncodingException;
	String getName();
}
