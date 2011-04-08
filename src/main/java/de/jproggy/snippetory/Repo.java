package de.jproggy.snippetory;

import java.util.Locale;

import de.jproggy.snippetory.impl.SnippetBuilder;
import de.jproggy.snippetory.spi.Encoding;
import de.jproggy.snippetory.spi.SyntaxID;
/**
 * This class offers some methods to ease access on templates. There 
 * @author Sir RotN
 */
public class Repo {
	
	public static Snippetory parse(CharSequence data) {
		return parse(data, Locale.US);
	}

	public static Snippetory parse(CharSequence data, Locale l) {
		return parse(data, Encodings.NULL, l);
	}

	public static Snippetory parse(CharSequence data, Encoding e, Locale l) {
		return new SnippetBuilder(l , e).parse(data);
	}
	
	public enum Syntax implements SyntaxID {
		XML_ALIKE,
		HIDDEN_BLOCKS;
		
		public String getName() { return name(); }
		
		public Snippetory parse(CharSequence data) {
			return parse(data, Locale.US);
		}
		
		public Snippetory parse(CharSequence data, Locale locale) {
			return parse(data, Encodings.NULL, locale);
		}
		
		public Snippetory parse(CharSequence data, Encoding e, Locale locale) {
			SnippetBuilder builder = new SnippetBuilder(locale, e);
			builder.setSyntax(de.jproggy.snippetory.spi.Syntax.REGISTRY.byName(getName()));
			return builder.parse(data);
		}
	}
}
