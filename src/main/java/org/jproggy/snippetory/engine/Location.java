package org.jproggy.snippetory.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.Transcoding;


public class Location {
	private final String name;
	private final Format[] formats;
	private Encoding enc;
	private String defaultVal;
	private final String fragment;
	private StringBuilder target;
	private final Location parent;
	private String delimiter;
	private String prefix;
	private String suffix;
	private Template template;

	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public Location(Location parent, String name, Map<String, String> attribs, String fragment, Locale l) {
		this.parent = parent;
		this.name = name;
		List<Format> formats = new ArrayList<Format>();
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
		this.formats = formats.toArray(new Format[formats.size()]);
		this.fragment = fragment;
	}
	@Override
	public String toString() {
		return toCharSequence().toString();
	}

	public CharSequence toCharSequence() {
		if (target != null) {
			if (suffix != null) return target.toString() + suffix;
			return target;
		}
		CharSequence f = format(defaultVal);
		if (f != null) return f;
		return fragment;
	}
	
	private CharSequence format(CharSequence value) {
		for (Format f: formats) {
			if (f.supports(value)) value = f.format(value);
		}
		return value;
	}
	private CharSequence toString(Object value) {
		if (value instanceof CharSequence) {
			return (String) value;
		}
		for (Format f: formats) {
			if (f.supports(value)) return f.format(value);
		}
		if (parent != null) return parent.toString(value);
		return String.valueOf(value);
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
		if (value instanceof EncodedData) {
			handleEncodedData((EncodedData)value);
		} else {
			getEncoding().escape(target, format(toString(value)));
		}
	}
	private void handleEncodedData(EncodedData value) {
		String sourceEnc = value.getEncoding();
		
		// normalize empty to null-encoding
		if (sourceEnc == null || sourceEnc.length() == 0) {
			sourceEnc = Encodings.NULL.getName();
		}
		Encoding myEnc = getEncoding();
		if (!sourceEnc.equals(myEnc.getName())) {
			transcode(value, sourceEnc, myEnc);
		} else {
			CharSequence formated = format(value.toCharSequence());
			if (formated instanceof Region) {
				((Region)formated).append(target);
			} else {
				target.append(formated);
			}
		}
	}
	private void transcode(EncodedData value, String sourceEnc, Encoding targetEnc) {
		for (Transcoding overwrite : EncodingRegistry.INSTANCE.getOverwrites(targetEnc)) {
			if (overwrite.supports(sourceEnc, targetEnc.getName())) {
				overwrite.transcode(target, format(value.toString()), sourceEnc, targetEnc.getName());
				return;
			}
		}
		targetEnc.transcode(target, format(value.toString()), sourceEnc);
	}
	public void clear() {
		target = null;
	}

	public String getName() {
		return name;
	}
	public Location getParent() {
		return parent;
	}
	public Encoding getEncoding() {
		if (enc == null) {
			if (parent != null) {
				enc = parent.getEncoding();
			} else {
				enc = Encodings.NULL;
			}
		}
		return enc;
	}
}
