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

import java.util.HashMap;
import java.util.Map;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Attributes.Types;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.spi.FormatFactory;


public final class FormatRegistry {
	private Map<String, FormatFactory> formats = new HashMap<String, FormatFactory>();

	private FormatRegistry() {
	}

	public void register(String name, FormatFactory value) {
		Attributes.REGISTRY.register(name, Types.FORMAT);
		formats.put(name, value);
	}

	public FormatConfiguration get(String name, String definition, TemplateContext ctx) {
		FormatFactory f = formats.get(name);
		if (f == null) {
			return null;
		}
		return f.create(definition, ctx);
	}

	public static final FormatRegistry INSTANCE = new FormatRegistry();
}
