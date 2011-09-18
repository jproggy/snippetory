package org.jproggy.snippetory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.engine.TemplateBuilder;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;

public class TemplateContext {
	private Locale locale = Locale.getDefault();
	private Syntax syntax = Syntax.REGISTRY.getDefault();
	private CharSequence data;
	private Map<String, String> baseAttribs = new HashMap<String, String>();

	public TemplateContext() {
		this.baseAttribs.put("date", "");
		this.baseAttribs.put("number", "");
	}

	public TemplateContext data(CharSequence data) {
		setData(data);
		return this;
	}
	public CharSequence getData() {
		return data;
	}
	public void setData(CharSequence data) {
		this.data = data;
	}

	public TemplateContext syntax(SyntaxID syntax) {
		return syntax(Syntax.REGISTRY.byName(syntax.getName()));
	}
	public TemplateContext syntax(Syntax syntax) {
		if (syntax == null) throw new NullPointerException();
		this.syntax = syntax;
		return this;
	}
	public Syntax getSyntax() {
		return syntax;
	}
	public void setSyntax(Syntax syntax) {
		this.syntax = syntax;
	}

	public TemplateContext encoding(String encoding) {
		return attrib("enc", encoding);
	}
	public TemplateContext encoding(Encoding encoding) {
		return encoding(encoding.getName());
	}

	public TemplateContext locale(Locale locale) {
		this.locale = locale;
		return this;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	public TemplateContext attrib(String name, String value) {
		this.baseAttribs.put(name, value);
		return this;
	}
	public Map<String, String> getBaseAttribs() {
		return baseAttribs;
	}
	public void setBaseAttribs(Map<String, String> baseAttribs) {
		this.baseAttribs = baseAttribs;
	}
	
	public Template parse() {
		return new TemplateBuilder().parse(this);
	}

	/**
	 * The data for the TemplateContext is searched on class path
	 */
	public TemplateContext readResource(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		return readResource(name, null);
	}

	public TemplateContext readResource(String name, ClassLoader test) {
		if (name == null) {
			throw new NullPointerException();
		}
		return new TemplateContext().data(ToString.resource(name, test));
	}
	
	public TemplateContext readFile(String fileName) {
		if (fileName == null) {
			throw new NullPointerException();
		}
		return new TemplateContext().data(ToString.file(fileName));
	}

	public TemplateContext readFile(File fileName) {
		return new TemplateContext().data(ToString.file(fileName));
	}
	
	/**
	 * 
	 * @param in 
	 */
	public TemplateContext readStream(InputStream in) {
		return new TemplateContext().data(ToString.stream(in));
	}

	public TemplateContext readReader(Reader in)  {
		return new TemplateContext().data(ToString.reader(in));
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
