package org.jproggy.snippetory.spi;

import java.io.IOException;


public interface Transcoding {
	void transcode(Appendable target, CharSequence value, String sourceEncoding, 
			String targetEncoding) throws IOException;

	boolean supports(String sourceEncoding, String targetEncoding);

}
