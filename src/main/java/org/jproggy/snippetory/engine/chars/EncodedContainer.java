package org.jproggy.snippetory.engine.chars;

import java.io.IOException;

import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.spi.EncodedData;

public class EncodedContainer implements EncodedData, CharSequence, SelfAppender {
	private final CharSequence data;
	private final String encoding;

	public EncodedContainer(CharSequence data, String encoding) {
		super();
		this.data = data;
		this.encoding = encoding;
	}

	@Override
	public <T extends Appendable> T appendTo(T to) {
		try {
			to.append(data);
			return to;
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
	}

	@Override
	public int length() {
		return data.length();
	}

	@Override
	public char charAt(int index) {
		return data.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return data.subSequence(start, end);
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	@Override
	public CharSequence toCharSequence() {
		return this;
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof EncodedContainer)) return false;
		EncodedContainer encodedContainer = (EncodedContainer)obj;
		if (!encoding.equals(encodedContainer.encoding)) return false;
		return toString().equals(encodedContainer.toString());
	}
	
	@Override
	public int hashCode() {
		return data.hashCode();
	}
}
