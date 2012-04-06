package org.jproggy.snippetory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.engine.NoDataException;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.engine.TemplateBuilder;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;

/**
 * The TemplateContext represents the configuration how templates are parsed. It
 * provides a fluent interface for inline creation as well as a bean interface
 * for convenient injection. Using the injection variant it's also easy to use a
 * subclass and use a different parsing mechanism or place an additional layer
 * of interceptors.
 * 
 * @author B. Ebertz
 */
public class TemplateContext {
	private Locale locale = Locale.getDefault();
	private Syntax syntax = Syntax.REGISTRY.getDefault();
	
	/**
	 * Omit initialization here to ensure TemplateContext to be cheap in default behavior
	 * and generate the cost of a map only when required.
	 */
	private Map<String, String> baseAttribs;
	
	private static final Map<String, String> DEFAULT_ATTRIBUTES; 

	static {
		Map<String,String> map = new HashMap<String, String>(2);
		map.put("date", "");
		map.put("number", "");
		DEFAULT_ATTRIBUTES = Collections.unmodifiableMap(map);
	}

	public TemplateContext syntax(SyntaxID syntax) {
		return syntax(Syntax.REGISTRY.byName(syntax.getName()));
	}

	public TemplateContext syntax(Syntax syntax) {
		setSyntax(syntax);
		return this;
	}

	public Syntax getSyntax() {
		return syntax;
	}

	public void setSyntax(Syntax syntax) {
		if (syntax == null)
			throw new NullPointerException();
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
		if (baseAttribs == null) {
			// if not initialized copy defaults
			baseAttribs = new LinkedHashMap<String, String>(DEFAULT_ATTRIBUTES);
		}
		this.baseAttribs.put(name, value);
		return this;
	}

	/**
	 * Returns the attributes intended to be set on the root node of a template created
	 * by one of the parse methods. This makes especially sense for inherited attributes.
	 * Expect the returned map to be unmodifiable. I.e. copy via copy constructor and 
	 * set newly if you want to extend it.
	 * @return
	 */
	public Map<String, String> getBaseAttribs() {
		if (baseAttribs == null) return DEFAULT_ATTRIBUTES;
		return baseAttribs;
	}

	public void setBaseAttribs(Map<String, String> baseAttribs) {
		this.baseAttribs = baseAttribs;
	}

	public Template parse(CharSequence data) {
		return TemplateBuilder.parse(this, data);
	}

	/**
	 * The data for the TemplateContext is searched on class path
	 */
	public Template parseResource(String name) {
		return parseResource(name, null);
	}

	public Template parseResource(String name, ClassLoader test) {
		if (name == null) {
			throw new NullPointerException();
		}
		return parse(ToString.resource(name, test));
	}

	public Template parseFile(String fileName) {
		if (fileName == null) {
			throw new NullPointerException();
		}
		return parse(ToString.file(fileName));
	}

	public Template parseFile(File fileName) {
		if (fileName == null) {
			throw new NullPointerException();
		}
		return parse(ToString.file(fileName));
	}

	/**
	 * 
	 * @param in
	 */
	public Template parseStream(InputStream in) {
		return parse(ToString.stream(in));
	}

	public Template parseReader(Reader in) {
		return parse(ToString.reader(in));
	}

	static class ToString {
		public static String resource(String name, ClassLoader test) {
			if (test != null) {
				InputStream in = test.getResourceAsStream(name);
				if (in == null)
					return resource(name, null);
				return stream(in);
			}
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader == null) {
				loader = ToString.class.getClassLoader();
			}
			if (loader == null) {
				loader = ClassLoader.getSystemClassLoader();
			}
			InputStream stream = loader.getResourceAsStream(name);
			if (stream == null) throw new NoDataException("Ressource " + name + " not found");
			return stream(stream);
		}

		public static String file(String fileName) {
			try {
				return stream(new FileInputStream(fileName));
			} catch (FileNotFoundException e) {
				throw new NoDataException(e);
			}
		}

		public static String file(File in) {
			try {
				return stream(new FileInputStream(in));
			} catch (FileNotFoundException e) {
				throw new NoDataException(e);
			}
		}

		public static String stream(InputStream in) {
			try {
				return reader(new InputStreamReader(in, "utf-8"));
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}

		public static String reader(Reader in) {
			try {
				char[] buffer = new char[255];
				StringWriter s = new StringWriter();
				int c;
				while ((c = in.read(buffer)) == buffer.length) {
					s.write(buffer);
				}
				if (c > 0)
					s.write(buffer, 0, c);
				return s.toString();
			} catch (IOException e) {
				throw new SnippetoryException(e);
			}
		}
	}
}
