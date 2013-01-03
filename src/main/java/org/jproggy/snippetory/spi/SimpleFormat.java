package org.jproggy.snippetory.spi;

public abstract class SimpleFormat implements Format, FormatConfiguration {

	@Override
	public Format getFormat(TemplateNode node) {
		return this;
	}

	@Override
	public void clear(TemplateNode location) {
	}

}
