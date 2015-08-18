package org.auriferous.bot.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigurableFile {
	protected Map<String, Configurable> configurables = new HashMap<String, Configurable>();
	protected File file;
	
	public ConfigurableFile() {
	}
	
	public ConfigurableFile(File file) {
		this.file = file;
	}
	
	public void addConfigurable(Configurable configurable) {
		String cName = configurable.getClass().getName();
		
		if (!this.configurables.containsKey(cName)) {
			ConfigurableEntry[] entries = getEntries(configurable);
			if (entries != null) {
				configurable.load(entries);
			} else {
				configurable.loadDefault();
			}
		} else {
			configurable.load(this.configurables.get(cName).getConfigurableEntries());
		}
		
		this.configurables.put(cName, configurable);
	}
	
	protected abstract ConfigurableEntry[] getEntries(Configurable configurable);
	
	public abstract void compile();
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	
	public boolean save() {
		return save(file);
	}
	
	public abstract boolean save(File file);
}
