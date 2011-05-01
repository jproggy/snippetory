package de.jproggy.snippetory.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.jproggy.snippetory.Snippetory;
import de.jproggy.snippetory.annotations.Encoded;
import de.jproggy.snippetory.spi.Encoding;
import de.jproggy.snippetory.spi.Format;

@Encoded
public class Variable {
	private final String name;
	private List<Format> formats = new ArrayList<Format>();
	private Encoding enc;
	private String defaultVal;
	private final String fragment;
	private StringBuilder target;
	private final Variable parent;
	public Variable getParent() {
		return parent;
	}
	private String delimiter;
	private String prefix;
	private String suffix;
	private Snippetory template;

	public Snippetory getTemplate() {
		return template;
	}
	public void setTemplate(Snippetory template) {
		this.template = template;
	}
	public Variable(Variable parent, String name, Map<String, String> attribs, String fragment, Locale l) {
		this.parent = parent;
		this.name = name;
		for (String attr: attribs.keySet()) {
			switch (Attributes.REGISTRY.type(attr))
			{
			case FORMAT:
				formats.add(FormatRegistry.INSTANCE.get(attr, attribs.get(attr), l));
				break;
			case DEFAULT:
				defaultVal = attribs.get(attr);
				break;
			case ENCODING:
				enc = EncodingRegistry.INSTANCE.get(attribs.get(attr));
				break;
			case DELIMITER:
				delimiter = attribs.get(attr);
				break;
			case PREFIX:
				prefix = attribs.get(attr);
				if (defaultVal == null) defaultVal = "";
				break;
			case SUFFIX:
				suffix = attribs.get(attr);
				if (defaultVal == null) defaultVal = "";
				break;
			}
		}
		this.fragment = fragment;
	}
	@Override
	public String toString() {
		if (target != null) {
			if (suffix != null) return target.toString() + suffix;
			return target.toString();
		}
		String f = format(defaultVal);
		if (f != null) return f;
		return fragment;
	}
	
	private String format(String value) {
		for (Format f: formats) {
			if (f.supports(value)) value = f.format(value);
		}
		if (parent != null) return parent.format(value);
		return  String.valueOf(value);
	}
	private String toString(Object value) {
		for (Format f: formats) {
			if (f.supports(value)) return f.format(value);
		}
		if (parent != null) return parent.toString(value);
		return String.valueOf(value);
	}
	private void escape(StringBuilder target, String value) {
		if (enc ==  null) {
			if (parent == null)	{
				target.append(value);
			} else {
				parent.escape(target, value);
			}
		} else {
			enc.escape(target, value);
		}
	}
	
	public void set(Object value) {
		clear();
		append(value);
	}
	public void append(Object value) {
		if (target==null) {
			target = prefix ==  null ? new StringBuilder() : new StringBuilder(prefix);
		} else {
			if (delimiter != null) target.append(delimiter);
		}
		Encoded encoded = null;
		if (value != null) {
			Class<? extends Object> valueType = value.getClass();
			encoded = valueType.getAnnotation(Encoded.class);
		}
		if (encoded != null) {
			String otherEnc = encoded.value();
			if (otherEnc.length() == 0) otherEnc = getEncoding(value);
			if (otherEnc != null && otherEnc.length() > 0 && 
					!otherEnc.equals(enc.getName())) {
				enc.transcode(target, value.toString(), otherEnc);
			} else {
				target.append(value);
			}
		} else {
			escape(target, format(toString(value)));
		}
	}
	private String getEncoding(Object value) {
		Class<? extends Object> valueType = value.getClass();
		try {
			for (Method m : valueType.getMethods()) {
				Object enc = m.getAnnotation(de.jproggy.snippetory.annotations.Encoding.class);
				if (enc !=  null) {
					if (m.getParameterTypes().length == 0) {
						if (String.class.equals(m.getReturnType())) {
							return (String)m.invoke(value);
						}
						if (Encoding.class.isAssignableFrom(m.getReturnType())) {
							Encoding encoding = (Encoding)m.invoke(value);
							if (encoding == null) return null;
							return encoding.getName();
						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new SnippetoryException(e);
		} catch (InvocationTargetException e) {
			throw new SnippetoryException(e);
		}
		return null;
	}
	public void clear() {
		target = null;
	}

	public String getName() {
		return name;
	}
	@de.jproggy.snippetory.annotations.Encoding
	public Encoding getEncoding() {
		return enc;
	}
}
