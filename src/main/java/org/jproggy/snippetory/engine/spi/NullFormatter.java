package org.jproggy.snippetory.engine.spi;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class NullFormatter implements FormatFactory {
	
	@Override
    public NullFormat create(String definition, TemplateContext ctx) {
		return new NullFormat(definition);
	}

	public static class NullFormat extends SimpleFormat {
		private final String value;
		
		public NullFormat(String value) {
			this.value = value;
		}
		
		@Override
		public Object format(TemplateNode location, Object value) {
			return this.value;
		}

		@Override
		public boolean supports(Object value) {
			return value == null;
		}
	}
}
