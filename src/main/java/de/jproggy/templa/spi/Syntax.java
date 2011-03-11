package de.jproggy.templa.spi;

import java.util.HashMap;
import java.util.Map;

import de.jproggy.templa.Templa;
import de.jproggy.templa.impl.HiddenBlocksSyntax;
import de.jproggy.templa.impl.Token;
import de.jproggy.templa.impl.XMLAlikeSyntax;

public interface Syntax {
	public class Registry {
		private Map<String, Syntax> reg =  new HashMap<String, Syntax>();
		private Registry() {
			register(Templa.Syntax.HIDDEN_BLOCKS, new HiddenBlocksSyntax());
			register(Templa.Syntax.XML_ALIKE, new XMLAlikeSyntax());
		}
		public void register(SyntaxID name, Syntax syntax) {
			reg.put(name.getName(), syntax);
		}
		public void register(String name, Syntax syntax) {
			reg.put(name, syntax);
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
