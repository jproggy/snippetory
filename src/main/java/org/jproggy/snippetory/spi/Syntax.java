package org.jproggy.snippetory.spi;

import java.util.HashMap;
import java.util.Map;

import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.impl.Token;
import org.jproggy.snippetory.impl.spi.HiddenBlocksSyntax;
import org.jproggy.snippetory.impl.spi.XMLAlikeSyntax;


public interface Syntax {
	public class Registry {
		private Map<String, Syntax> reg =  new HashMap<String, Syntax>();
		private Registry() {
			register(Syntaxes.HIDDEN_BLOCKS, new HiddenBlocksSyntax());
			register(Syntaxes.XML_ALIKE, new XMLAlikeSyntax());
		}
		public void register(SyntaxID name, Syntax syntax) {
			reg.put(name.getName(), syntax);
		}
		public Syntax byName(String name) {
			return reg.get(name);
		}
		public Syntax getDefault() {
			return new XMLAlikeSyntax();
		}
	}
	interface Parser {
		boolean hasNext();
		Token next();
		CharSequence getData();
		int getPosition();
		void jumpTo(int position);
	}
	
	Parser parse(CharSequence data);
	Parser takeOver(Parser data);
	
	Registry REGISTRY = new Registry();
	enum TokenType {
		BlockStart, BlockEnd, Field, Syntax, TemplateData
	}

}
