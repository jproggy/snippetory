package de.jproggy.snippetory.spi;

import java.util.HashMap;
import java.util.Map;

import de.jproggy.snippetory.Repo;
import de.jproggy.snippetory.impl.HiddenBlocksSyntax;
import de.jproggy.snippetory.impl.Token;
import de.jproggy.snippetory.impl.XMLAlikeSyntax;

public interface Syntax {
	public class Registry {
		private Map<String, Syntax> reg =  new HashMap<String, Syntax>();
		private Registry() {
			register(Repo.Syntax.HIDDEN_BLOCKS, new HiddenBlocksSyntax());
			register(Repo.Syntax.XML_ALIKE, new XMLAlikeSyntax());
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
