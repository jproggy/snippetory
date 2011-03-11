package de.jproggy.templa.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.jproggy.templa.Template;
import de.jproggy.templa.annotations.Encoded;
import de.jproggy.templa.spi.Encoding;
import de.jproggy.templa.spi.Format;

@Encoded
public class Variable {
	private final String name;
	private List<Format> formats = new ArrayList<Format>();
	private Encoding enc;
	private String defaultVal;
	private StringBuilder target;
	private final Variable parent;
	public Variable getParent() {
		return parent;
	}
	private String delimiter;
	private Template template;

	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
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
		if (defaultVal == null) defaultVal = fragment;
	}
	@Override
	public String toString() {
		if (target != null) return target.toString();
		return  format(defaultVal);
	}
	
	private String format(Object value) {
		for (Format f: formats) {
			if (f.supports(value)) value = f.format(value);
		}
		if (parent != null) return parent.format(value);
		return  String.valueOf(value);
	}
	private void encode(StringBuilder target, String value) {
		if (enc ==  null) {
			if (parent == null)	target.append(value);
			parent.encode(target, value);
		} else {
			enc.encode(target, value);
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
			encode(target, format(value));
		}
	}
	private String getEncoding(Object value) {
		Class<? extends Object> valueType = value.getClass();
		try {
			for (Method m : valueType.getMethods()) {
				Object enc = m.getAnnotation(de.jproggy.templa.annotations.Encoding.class);
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
			throw new TemplaException(e);
		} catch (InvocationTargetException e) {
			throw new TemplaException(e);
		}
		return null;
	}
	public void clear() {
		target = null;
	}

	public String getName() {
		return name;
	}
	@de.jproggy.templa.annotations.Encoding
	public Encoding getEncoding() {
		return enc;
	}
}
