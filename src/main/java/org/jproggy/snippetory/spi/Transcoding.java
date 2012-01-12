package org.jproggy.snippetory.spi;


public interface Transcoding {
	void transcode(StringBuilder target, CharSequence value, String sourceEncoding, String targetEncoding);

	boolean supports(String sourceEncoding, String targetEncoding);

}
