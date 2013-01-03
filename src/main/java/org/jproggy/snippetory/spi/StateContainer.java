package org.jproggy.snippetory.spi;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class StateContainer<V> {
	private final Map<TemplateNode, V> data = new WeakHashMap<TemplateNode, V>();
	private final KeyResolver resolver;
	
	public StateContainer(KeyResolver resolver) {
		super();
		this.resolver = resolver;
	}

	protected abstract V createValue(TemplateNode key);
	
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
	
	public interface KeyResolver {
		public static final KeyResolver PARENT = new KeyResolver() {
			@Override
            public TemplateNode resolve(TemplateNode org) {
				return org.getParent();
			}
		};
		
		public static final KeyResolver ROOT = new KeyResolver() {
			@Override
            public TemplateNode resolve(TemplateNode org) {
				while (org.getParent() != null) org = org.getParent();
				return org;
			}
		};
		
		public static final KeyResolver NONE = new KeyResolver() {
			@Override
            public TemplateNode resolve(TemplateNode org) {
				return org;
			}
		};
		
		TemplateNode resolve(TemplateNode org);
	}
}
