package de.jproggy.templa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Locale;

import de.jproggy.templa.impl.TemplaException;
import de.jproggy.templa.impl.TemplateBuilder;
import de.jproggy.templa.spi.Encoding;
import de.jproggy.templa.spi.SyntaxID;
/**
 * This class offers some methods to ease access on templates. There 
 * @author Sir RotN
 */
public class Templa {
	
	public static Template fromString (CharSequence data) {
		return fromString(data, Locale.US);
	}

	public static Template fromString(CharSequence data, Locale l) {
		return fromString(data, Encodings.NULL, l);
	}

	public static Template fromString(CharSequence data, Encoding e, Locale l) {
		return new TemplateBuilder(l , e).parse(data);
	}
	
	public static String readResource(String name) {
		return readResource(name, null);
	}
	
	public static String readResource(String name, ClassLoader test) {
		if (test != null) {
			InputStream in = test.getResourceAsStream(name);
			if (in == null) return readResource(name);
			return toString(in);
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) { 
			loader = Templa.class.getClassLoader();
		}
		if (loader == null) { 
			loader = ClassLoader.getSystemClassLoader();
		}
		return toString(loader.getResourceAsStream(name));
	}
	
	public static String toString(String fileName) {
		if (fileName == null) {
			return null;
		}
		try {
			return toString(new FileInputStream(fileName));
		} catch (IOException e) {
			throw new TemplaException(e);
		}
	}
	
	public static String toString(File in) {
		if (in == null) {
			return null;
		}
		try {
			return toString(new FileInputStream(in));
		} catch (IOException e) {
			throw new TemplaException(e);
		}
	}
	
	public static String toString(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			return toString(new InputStreamReader(in, "utf-8"));
		} catch (IOException e) {
			throw new TemplaException(e);
		}
	}

	public static String toString(Reader in)  {
		if (in == null) {
			return null;
		}
		try {
			char[] buffer = new char[255];
			StringWriter s = new StringWriter();
			int c;
			while ((c = in.read(buffer)) == buffer.length) {
				s.write(buffer);
			}
			if (c > 0) s.write(buffer, 0, c);
			return s.toString();
		} catch (IOException e) {
			throw new TemplaException(e);
		}
	}
	
	public enum Syntax implements SyntaxID {
		XML_ALIKE,
		HIDDEN_BLOCKS;
		
		public String getName() { return name(); }
		
		public Template fromString(CharSequence data) {
			return fromString(data, Locale.US);
		}
		
		public Template fromString(CharSequence data, Locale locale) {
			return fromString(data, Encodings.NULL, locale);
		}
		
		public Template fromString(CharSequence data, Encoding e, Locale locale) {
			TemplateBuilder builder = new TemplateBuilder(locale, e);
			builder.setSyntax(de.jproggy.templa.spi.Syntax.REGISTRY.byName(getName()));
			return builder.parse(data);
		}
	}
}
