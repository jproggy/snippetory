package org.jproggy.snippetory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jproggy.snippetory.engine.IncompatibleEncodingException;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.spi.Encoding;

/**
 * Provides direct access to the predefined encodings.
 * @author B. Ebertz
 */
public enum Encodings implements Encoding {
	/**
	 * It's assumed that Snippetory is used in a modern Unicode based environment. Though,
	 * only a minimal escaping is done: 
	 * <table width="150">
	 * <tr><td>&lt;</td><td>--></td><td>&amp;lt;</td></tr>
	 * <tr><td>&amp;</td><td>--></td><td>&ampamp;</td></tr>
	 * </table>
	 * <br />
	 * As xml is a compound format, i.e. it can contain other formats, almost each other 
	 * will be placed within without any transcoding. Only on plain text the normal escaping
	 * is applied.
	 */
	xml {
		@Override
		public void escape(Appendable target, CharSequence val) throws IOException {
			for (int i = 0; i < val.length(); i++) {
				char c = val.charAt(i);
				if (c == '<') {
					target.append("&lt;");
				} else if (c == '&') {
					target.append("&amp;");
				} else {
					target.append(c);
				}
			}
		}
		@Override
		public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
			if (plain.name().equals(encodingName)) {
				escape(target, value);
			} else {
				append(target, value);
			}
		}
	},
	
	/**
	 * html is derived from xml. It just converts line breaks to &lt;br />-tags to enable
	 * transporting of simple formatting within the data bound. Be aware: this applies 
	 * to data bound, not to some kind of source code like in html pages, so we do not 
	 * break with the good practice of separating the layout of source code and it's 
	 * resulting appearance.
	 */
	html {
		@Override
		public void escape(Appendable target, CharSequence val) throws IOException {
			for (int i = 0; i < val.length(); i++) {
				char c = val.charAt(i);
				if (c == '<') {
					target.append("&lt;");
				} else if (c == '&') {
					target.append("&amp;");
				} else if (c == 10) {
					target.append("<br />");
					if (i + 1 < val.length() && val.charAt(i + 1) == 13)
						i++;
				} else if (c == 13) {
					target.append("<br />");
					if (i + 1 < val.length() && val.charAt(i + 1) == 10)
						i++;
				} else {
					target.append(c);
				}
			}
		}
		@Override
		public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
			if (plain.name().equals(encodingName)) {
				escape(target, value);
			} else {
				append(target, value);
			}
		}
	},
	/**
	 * Applies url encoding to the data
	 */
	url {
		@Override
		public void escape(Appendable target, CharSequence val) throws IOException {
			try {
				target.append(URLEncoder.encode(val.toString(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				throw new SnippetoryException(e);
			}
		}
	},
	/**
	 * Most C-based languages have almost the same rules. This fits at least Java and
	 * JavaScript.
	 */
	string {
		@Override
		public void escape(Appendable target, CharSequence val) throws IOException {
			for (int i = 0; i < val.length(); i++) {
				char ch = val.charAt(i);
				if (ch < 32) {
					switch (ch) {
					case '\b':
						target.append('\\');
						target.append('b');
						break;
					case '\n':
						target.append('\\');
						target.append('n');
						break;
					case '\t':
						target.append('\\');
						target.append('t');
						break;
					case '\f':
						target.append('\\');
						target.append('f');
						break;
					case '\r':
						target.append('\\');
						target.append('r');
						break;
					default:
						// ignore no practical meaning
						break;
					}
				} else {
					switch (ch) {
					case '\'':
					case '"':
					case '\\':
						target.append('\\');
					}
					target.append(ch);
				}
			}
		}
		@Override
		public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
			if (html_string.name().equals(encodingName) || NULL.name().equals(encodingName)) {
				append(target, value);
			} else {
				escape(target, value);
			}
		}
	},
	/**
	 * In JavaScript I've sometimes data that is transported in a string before it's 
	 * displayed as html.
	 */
	html_string {
		@Override
		public void escape(Appendable target, CharSequence val) throws IOException {
			StringBuilder tmp = new StringBuilder();
			string.escape(tmp, val);
			html.escape(target, tmp.toString());
		}
		@Override
		public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
			if (xml.name().equals(encodingName) || html.name().endsWith(encodingName)) {
				string.escape(target, value);
			}
			else if (string.name().equals(encodingName)) {
				// I don't expect this to have practical use. But it adds additional risk
				// so breaking seems right.
				throw new IncompatibleEncodingException("Can't check if content might be html");
			} 
			else {
				super.transcode(target, value, encodingName);
			}
			
		}
	},
	/**
	 * Plain text. You will get an IllegalEncodingExcption if you try to bind encoded data 
	 * to it.
	 */
	plain {
		@Override
		public void escape(Appendable target, CharSequence val) throws IOException {
			append(target, val);
		}
	},
	/**
	 * The wild card encoding. Fits to any other, any other fits to this. Sometimes it's 
	 * necessary to work around the checks. But in general you should not use it.
	 */
	NULL {
		@Override
		public void escape(Appendable target, CharSequence val) throws IOException {
			append(target, val);
		}
		@Override
		public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
			escape(target, value);
		}
	}
	;

	@Override
	public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
		if (NULL.name().equals(encodingName)) {
			append(target, value);
		}
		if (plain.name().equals(encodingName)) {
			escape(target, value);
		}
		throw new IncompatibleEncodingException("can't convert encoding "
				+ encodingName + " into " + name());
	}
	@Override
	public String getName() {
		return name();
	}
	
	public TemplateContext context() {
		return new TemplateContext().encoding(this);
	}
	
	public Template parse(CharSequence data) {
		return context().parse(data);
	}
	private static void append(Appendable target, CharSequence value) throws IOException {
		if (value instanceof Region) {
			((Region)value).appendTo(target);
		} else {
			target.append(value);
		}
	}
}
