package org.auriferous.bot.config;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigurableEntry<N,V> {
	private N key;
	private V value;
	
	private List<ConfigurableEntry> children = new ArrayList<ConfigurableEntry>();
	
	public ConfigurableEntry(N key) {
		this(key, null);
	}
	
	public ConfigurableEntry(N key, V value) {
		this.key = key;
		this.value = value;
	}

	public abstract ConfigurableEntry<N,V> copy();
	
	public void setKey(N key) {
		this.key = key;
	}
	
	public void setValue(V value) {
		this.value = value;
	}
	
	public N getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
	
	public List<ConfigurableEntry> getChildren() {
		return children;
	}
	
	public void setChildren(List<ConfigurableEntry> children) {
		this.children = children;
	}
	
	public Object get(Object path, Object defaultValue) {
		List<ConfigurableEntry<Object,Object>> l = get(path);
		if (l.size() > 0)
			return l.get(0).getValue();
		
		return defaultValue;
	}
	
	public abstract List<ConfigurableEntry<Object,Object>> get(Object path);
}