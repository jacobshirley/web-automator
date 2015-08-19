package org.auriferous.bot.config;

import java.io.File;
import java.io.IOException;
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
			ConfigurableEntry entries = getEntries(configurable);
			if (entries != null) {
				configurable.load(entries);
			} else {
				configurable.loadDefault();
			}
		} else {
			configurable.load(this.configurables.get(cName).getConfiguration());
		}
		
		this.configurables.put(cName, configurable);
	}
	
	private void merge(ConfigurableEntry entry1, ConfigurableEntry entry2) {
		
	}
	
	
	protected abstract ConfigurableEntry getEntries(Configurable configurable);
	
	public abstract void compile();
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	
	public boolean save() throws IOException {
		if (file != null)
			return save(file);
		else throw new IOException("Please make sure you have set an output path. Use setPath.");
	}
	
	public abstract boolean save(File file) throws IOException;
}
