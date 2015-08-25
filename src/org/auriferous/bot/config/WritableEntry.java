package org.auriferous.bot.config;

import java.util.List;

public class WritableEntry<N,V> extends ConfigurableEntry<N, V>{

	public WritableEntry(N key) {
		super(key);
	}
	
	public WritableEntry(N key, V value) {
		super(key, value);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ConfigurableEntry<N, V> copy() {
		return new WritableEntry<N,V>(getKey(), getValue());
	}
	
	@Override
	public Object get(Object path, Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConfigurableEntry<Object, Object>> get(Object path) {
		// TODO Auto-generated method stub
		return null;
	}

}
