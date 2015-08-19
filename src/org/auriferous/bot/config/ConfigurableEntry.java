package org.auriferous.bot.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigurableEntry<N,V> {
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

	public ConfigurableEntry<N,V> copy() {
		return new ConfigurableEntry<N,V>(key, value);
	}
	
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
	
	public List<ConfigurableEntry<Object,Object>> get(Object key) {
		List<ConfigurableEntry<Object,Object>> results = new ArrayList<ConfigurableEntry<Object,Object>>();
		for (ConfigurableEntry<Object,Object> cE : children) {
			if (cE.key.equals(key)) {
				results.add(cE);
			}
		}
		return results;
	}
}
