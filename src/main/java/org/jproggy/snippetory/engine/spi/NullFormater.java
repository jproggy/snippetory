package org.jproggy.snippetory.engine.spi;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatFactory;

public class NullFormater implements FormatFactory {
	
	public Format create(String definition, TemplateContext ctx) {
		return new NullFormat(definition);
	}

	private static class NullFormat implements Format {
		private final String value;
		
		public NullFormat(String value) {
			this.value = value;
		}
		
		@Override
		public Object format(Object value) {
			return this.value;
		}

		@Override
		public boolean supports(Object value) {
			return value == null;
		}
	}
}
