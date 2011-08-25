package de.jproggy.snippetory.spi;


public interface Transcoding {
	void transcode(StringBuilder target, String value, String sourceEncoding, String targetEncoding);

	boolean supports(String sourceEncoding, String targetEncoding);

}
