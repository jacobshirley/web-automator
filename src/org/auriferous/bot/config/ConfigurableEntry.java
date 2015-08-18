package org.auriferous.bot.config;

public class ConfigurableEntry {
	private String key;
	private String value;
	private ConfigurableEntry[] children;
	
	public ConfigurableEntry(String key, String value) {
		this(key, value, null);
	}
	
	public ConfigurableEntry(String key, String value, ConfigurableEntry[] children) {
		this.key = key;
		this.value = value;
		
		this.children = children;
	}
	
	public ConfigurableEntry copy() {
		return new ConfigurableEntry(key, value, children);
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setChildren(ConfigurableEntry[] children) {
		this.children = children;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public ConfigurableEntry[] getChildren() {
		return children;
	}
}
