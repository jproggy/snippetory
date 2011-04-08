package de.jproggy.snippetory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import de.jproggy.snippetory.impl.SnippetoryException;

public class ToString {
	public static String resource(String name) {
		return resource(name, null);
	}
	
	public static String resource(String name, ClassLoader test) {
		if (test != null) {
			InputStream in = test.getResourceAsStream(name);
			if (in == null) return resource(name);
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
		if (fileName == null) {
			return null;
		}
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
