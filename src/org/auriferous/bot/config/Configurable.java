package org.auriferous.bot.config;

public interface Configurable {
	public void loadDefault();
	
	public void load(ConfigurableEntry<?,?> configuation);
	public ConfigurableEntry<?,?> getConfiguration();
}
