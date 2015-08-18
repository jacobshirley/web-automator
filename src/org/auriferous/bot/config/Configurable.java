package org.auriferous.bot.config;

public interface Configurable {
	public void loadDefault();
	
	public void load(ConfigurableEntry[] configEntries);
	public ConfigurableEntry[] getConfigurableEntries();
}
