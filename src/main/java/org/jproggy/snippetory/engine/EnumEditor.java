package org.jproggy.snippetory.engine;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumEditor extends PropertyEditorSupport {
	private final String[] tags;
	private final Method resolver;
	
	public EnumEditor(Class<Enum<?>> type) {
		try {
			Method method = type.getMethod("values");
			Enum<?>[] values = (Enum<?>[])method.invoke(null);
			tags = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				tags[i] = values[i].name();
			}
			resolver = type.getMethod("valueOf", String.class);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new SnippetoryException(e);
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try {
			setValue(resolver.invoke(null, text));
		} catch (IllegalAccessException e) {
			throw new SnippetoryException(e);
		} catch (InvocationTargetException e) {
			throw new SnippetoryException(e.getTargetException());
		}
	}

	@Override
	public String getJavaInitializationString() {
		return getValue().getClass().getSimpleName() + '.' + getAsText();
	}

	@Override
	public String getAsText() {
		return ((Enum<?>)getValue()).name();
	}

	@Override
	public String[] getTags() {
		return tags;
	}

}
