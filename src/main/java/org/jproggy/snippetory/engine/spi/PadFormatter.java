package org.jproggy.snippetory.engine.spi;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class PadFormatter implements FormatFactory {

	@Override
	public FormatConfiguration create(String definition, TemplateContext ctx) {
		int width = Integer.parseInt(definition);
		return new PadFormat(width);
	}
	
	public enum Alignment {left, right}
	
	public static class PadFormat extends SimpleFormat {
		private int width;
		private Alignment align = Alignment.left;
		private String fill = "                       ";
		
		public PadFormat(int width) {
			this.width = width;
		}
		
		public void setAlign(Alignment val) {
			if (val == null) throw new NullPointerException();
			align = val;
		}
		
		public void setFill(String fill) {
			this.fill = fill;
		}

		@Override
		public Object format(TemplateNode location, Object value) {
			if (CharDataSupport.length(value) >= width) {
				return value;
			}
			String v = value.toString();
			String b = fill(width - v.length());
			return (align != Alignment.right) ? v + b : b + v;
		}

		private String fill(int i) {
			while (fill.length() < i) {
				fill += fill;
			}
			return fill.substring(0, i);
		}

		@Override
		public boolean supports(Object value) {
			return CharDataSupport.isCharData(value);
		}
	}

}
