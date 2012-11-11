package org.jproggy.snippetory.engine.spi;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.VoidFormat;

public class DefaultFormater implements FormatFactory {

	@Override
	public Format create(String definition, TemplateContext ctx) {
		return new DefaultFormat(definition);
	}
	
	private static class DefaultFormat implements VoidFormat {
		private final String value;
		
		private DefaultFormat(String value) {
			this.value = value;
		}

		@Override
		public Object format(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean supports(Object value) {
			return false;
		}

		@Override
		public Object formatVoid() {
			return value;
		}
	}
}
