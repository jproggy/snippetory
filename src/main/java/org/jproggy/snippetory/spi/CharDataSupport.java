package org.jproggy.snippetory.spi;

import java.io.IOException;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.engine.chars.EncodedContainer;
import org.jproggy.snippetory.engine.chars.SelfAppender;

public class CharDataSupport {
	public static boolean isCharData(Object value) {
		return value instanceof EncodedData || value instanceof CharSequence;
	}
	
	public static CharSequence toCharSequence(Object chars) {
		if (chars instanceof CharSequence) {
			return (CharSequence)chars;
		}
		return ((EncodedData)chars).toCharSequence();
	}
	
	public static String getEncoding(Object chars) {
		if (chars instanceof EncodedData) {
			((EncodedData)chars).getEncoding();
		}
		return Encodings.plain.getName();
	}
	
	public static EncodedData toEncodedData(Object chars) {
		if (chars instanceof EncodedData) {
			return (EncodedData)chars;
		}
		if (chars instanceof CharSequence) {
			return new EncodedContainer((CharSequence)chars, Encodings.plain.getName());
		}
		throw new SnippetoryException("No character data: " + chars);
	}
	
	public static EncodedData toEncodedData(Object chars, String encoding) {
		if (chars instanceof EncodedData) {
			EncodedData data = (EncodedData)chars;
			if (getEncoding(chars).equals(encoding)) return data;
			return new EncodedContainer(data.toCharSequence(), encoding);
		}
		if (chars instanceof CharSequence) {
			return new EncodedContainer((CharSequence)chars, encoding);
		}
		throw new SnippetoryException("No character data: " + chars);
	}
	
	public static void append(Object base, Object... values) {
		try {
			Appendable append = toAppandable(base);
			for (Object val: values) {
				if (val instanceof SelfAppender) {
					if (base instanceof Appendable) {
						((SelfAppender)val).appendTo(append);
					}
				} else if (isCharData(val)) {
					append.append(toCharSequence(val));
				} else {
					append.append(val.toString());
				}
			}
		} catch (IOException e) {
			throw new SnippetoryException(e);
		}
	}

	private static Appendable toAppandable(Object base) {
		if (base instanceof Appendable) return (Appendable)base;
		return new StringBuilder().append(base);
	}
}
