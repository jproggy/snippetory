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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.spi.EncodedData;

public class ConditionalRegion extends DataSinks implements EncodedData {
	private final Set<String> names;
	private final Map<String, Region> children;
	private boolean appendMe;

	public ConditionalRegion(Location formatter, List<DataSink> parts, Map<String, Region> children) {
		super(parts, formatter);
		names = names();
		this.children = children;
	}
	
	private ConditionalRegion(ConditionalRegion template, Location parent) {
		super(template, template.getPlaceholder().cleanCopy(parent));
		names = names();
		this.children =  new HashMap<String, Region>();
		for (Map.Entry<String, Region> entry: template.children.entrySet()) {
			this.children.put(entry.getKey(), entry.getValue().cleanCopy(getPlaceholder()));
		}
		appendMe =  false;
	}

	@Override
	public void set(String name, Object value) {
		if (names.contains(name)) {
			super.set(name, value);
			if (value != null) appendMe =  true;
		}
	}

	@Override
	public void append(String name, Object value) {
		if (names.contains(name)) {
			super.append(name, value);
			if (value != null) appendMe =  true;
		}
	}

	@Override
	public Set<String> regionNames() {
		return children.keySet();
	}
	
	@Override
	public Region getChild(String name) {
		Region child = children.get(name);
		if (child != null) child = child.cleanCopy(getPlaceholder());
 		return child;
	}
	
	@Override
	public void clear() {
		super.clear();
		getPlaceholder().clear();
		appendMe = false;
	}

	@Override
	public ConditionalRegion cleanCopy(Location parent) {
		return new ConditionalRegion(this, parent);
	}

	@Override
	public CharSequence format() {
		Location placeholder = getPlaceholder();
		if (appendMe) {
			placeholder.set(this);
		}
		return placeholder.format();
	}
	
	@Override
	public CharSequence toCharSequence() {
		return this;
	}

	@Override
	public String getEncoding() {
		return getPlaceholder().getEncoding();
	}

}
