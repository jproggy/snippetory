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
			}
		}
		this.fragment = fragment;
	}
	@Override
	public String toString() {
		if (target != null) return target.toString();
		Object f = tryFormat(defaultVal);
		if (f instanceof String) {
			return (String) f;
		}
		return fragment;
	}
	
	private String format(Object value) {
		value = formatInternal(value);
		if (parent != null) return parent.format(value);
		return  String.valueOf(value);
	}
	private Object tryFormat(Object value) {
		value = formatInternal(value);
		if (parent != null) return parent.tryFormat(value);
		return  value;
	}
	private Object formatInternal(Object value) {
		for (Format f: formats) {
			if (f.supports(value)) value = f.format(value);
		}
		return value;
	}
	private void escape(StringBuilder target, String value) {
		if (enc ==  null) {
			if (parent == null)	target.append(value);
			parent.escape(target, value);
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
			target = new StringBuilder();
		} else {
			if (delimiter != null) target.append(delimiter);
		}
		Class<? extends Object> valueType = value.getClass();
		Encoded encoded = valueType.getAnnotation(Encoded.class);
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
			escape(target, format(value));
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
