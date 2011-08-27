package org.jproggy.snippetory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Locale;

import org.jproggy.snippetory.impl.SnippetoryException;

/**
 * This class offers some methods to ease access on templates. There 
 * @author Sir RotN
 */
public class Repo {
	
	public static Template parse(CharSequence data) {
		return read(data).parse();
	}

	public static Template parse(CharSequence data, Locale l) {
		return read(data).locale(l).parse();
	}

	public static Parser read(CharSequence data) {
		return new Parser(data);
	}
	
	public static Parser readResource(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		return readResource(name, null);
	}

	public static Parser readResource(String name, ClassLoader test) {
		if (name == null) {
			throw new NullPointerException();
		}
		return new Parser(ToString.resource(name, test));
	}
	
	public static Parser readFile(String fileName) {
		if (fileName == null) {
			throw new NullPointerException();
		}
		return new Parser(ToString.file(fileName));
	}

	public static Parser readFile(File fileName) {
		return new Parser(ToString.file(fileName));
	}
	
	public static Parser readStream(InputStream in) {
		return new Parser(ToString.stream(in));
	}

	public static Parser readReader(Reader in)  {
		return new Parser(ToString.reader(in));
	}
	
	private static class ToString {
		public static String resource(String name, ClassLoader test) {
			if (test != null) {
				InputStream in = test.getResourceAsStream(name);
				if (in == null) return resource(name, null);
				return stream(in);
			}
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader == null) { 
				loader = Repo.class.getClassLoader();
			}
			if (loader == null) { 
				loader = ClassLoader.getSystemClassLoader();
			}
			return stream(loader.getResourceAsStream(name));
		}
		
		public static String file(String fileName) {
			try {
				return stream(new FileInputStream(fileName));
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}
		
		public static String file(File in) {
			if (in == null) {
				return null;
			}
			try {
				return stream(new FileInputStream(in));
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}
		
		public static String stream(InputStream in) {
			if (in == null) {
				return null;
			}
			try {
				return reader(new InputStreamReader(in, "utf-8"));
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}

		public static String reader(Reader in)  {
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
				throw new SnippetoryException(e);
			}
		}
	}
}
