package org.auriferous.bot.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigFile {
	private List<Configurable> configurables = new ArrayList<Configurable>();

	public void put(String xpath, String value) {
		
	}
	
	public String get(String xpath) {
		return null;
	}
	
	public void addConfigurable(Configurable configurable) {
		this.configurables.add(configurable);
	}
}
