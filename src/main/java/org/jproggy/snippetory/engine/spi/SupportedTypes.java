package org.jproggy.snippetory.engine.spi;

public class SupportedTypes {
	private final Class<?>[] types;

	public SupportedTypes(Class<?>... types) {
		super();
		this.types = types;
	}
	
	public boolean isSupported(Object value) {
		for (Class<?> type : types) {
			if (type.isInstance(value)) return true;
		}
		return false;
	}
}
