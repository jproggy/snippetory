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

package org.jproggy.snippetory.spi;

import java.util.Map;
import java.util.WeakHashMap;

import org.jproggy.snippetory.engine.spi.ToggleFormatter;

/**
 * Simplifies handling based on TemplateNode. The problem with this is, that the state is not
 * bound directly to one node, but rather to a parent of the node or a node even higher in this hirarchy.
 * In some cases it is necessary to collect data over more than one node. Like counters for instance.
 * It handles resolving the right key and creating new objects.
 * 
 * @param <V> is the type of the values kept in this container. Typically this is a {@link Format}.
 * @author B. Ebertz
 * @see ToggleFormatter
 */
public abstract class StateContainer<V> {
	private final Map<TemplateNode, V> data = new WeakHashMap<TemplateNode, V>();
	private final KeyResolver resolver;
	
	/**
	 * 
	 */
	public StateContainer(KeyResolver resolver) {
		super();
		this.resolver = resolver;
	}
	
	/**
	 * Create a new instance to handle your state. Typically this will create a 
	 * {@link Format}. This method is only called if there is 
	 */
	protected abstract V createValue(TemplateNode key);
	
	/**
	 * Create a
	 */
	public V get(TemplateNode key) {
		key = resolver.resolve(key);
		if (!data.containsKey(key)) {
			V value = createValue(key);
			data.put(key, value);
			return value;
		}
		return data.get(key);
	}
	
	public void clear(TemplateNode key) {
		data.remove(resolver.resolve(key));
	}

	public void put(TemplateNode key, V value) {
		key = resolver.resolve(key);
		data.put(key, value);
	}
	
	/**
	 * Calculates the node to bind the state to based on node the node provided.
	 */
	public static abstract class KeyResolver {
		public static final KeyResolver PARENT = new KeyResolver() {
			@Override
            public TemplateNode resolve(TemplateNode org) {
				return org.getParent();
			}
		};
		
		/**
		 * Binds the state to an instance of a template even but not on a copy
		 * created by calling {@link Template#get} without parameter.
		 * Be aware such a copy uses same instance of FormatConfiguration
		 * but should be completely independed.
		 */
		public static final KeyResolver ROOT = new KeyResolver() {
			@Override
            public TemplateNode resolve(TemplateNode org) {
				while (org.getParent() != null) org = org.getParent();
				return org;
			}
		};
		
		public static KeyResolver up(int levels) {
			return new LevelNavigator(levels);
		}
		
		private static class LevelNavigator extends KeyResolver {
			final int levels;
			public LevelNavigator(int levels) {
				super();
				this.levels = levels;
			}
			@Override
			public TemplateNode resolve(TemplateNode org) {
				for (int i = 0; (i < levels) && (org != null); i++) {
					org = org.getParent();
				}
				return org;
			}
			
		}
		
		/**
		 * @deprecated State bound to a single node can be maintained conveniently
		 * by returning a new instance on each call of 
		 * {@link FormatConfiguration#getFormat(TemplateNode)} 
		 */
		@Deprecated
		public static final KeyResolver NONE = new KeyResolver() {
			@Override
            public TemplateNode resolve(TemplateNode org) {
				return org;
			}
		};
		
		public abstract TemplateNode resolve(TemplateNode org);
	}
}
