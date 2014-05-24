/*******************************************************************************
 * Copyright (c) 2011-2012 JProggy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE,
 * NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE
 *******************************************************************************/
package org.jproggy.snippetory.engine;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumEditor extends PropertyEditorSupport {
	private final String[] tags;
	private final Method resolver;

	public EnumEditor(Class<Enum<?>> type) {
		try {
			Enum<?>[] values = type.getEnumConstants();
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
