package org.auriferous.bot.shared.data.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.auriferous.bot.shared.data.DataEntry;
import org.auriferous.bot.shared.data.RootEntry;
import org.auriferous.bot.shared.data.xml.XMLDataStore;

public class ClassDataStore extends XMLDataStore{
	protected Map<String, Configurable> configurables = new HashMap<String, Configurable>();
	
	public ClassDataStore() {
	}
	
	public ClassDataStore(File file) throws IOException {
		super(file);
	}
	
	public ClassDataStore(String path) throws IOException {
		super(path);
	}
	
	public void addConfigurable(Configurable configurable) {
		synchronized (configurable) {
			String cName = configurable.getClass().getName();
			
			if (!this.configurables.containsKey(cName)) {
				DataEntry entry = getEntry(cName, configurable);
				if (entry == null) {
					entry = new RootEntry(cName);
					getRootEntry().add(entry);
				}
				
				configurable.load(entry);
			} else {
				Configurable configurable2 = this.configurables.get(cName);
				
				DataEntry root = getEntry(cName, configurable2);
				configurable2.save(root);
				
				configurable.load(root);
			}
			
			this.configurables.put(cName, configurable);
		}
	}
	
	private DataEntry getEntry(Configurable configurable) {
		return getEntry(configurable.getClass().getName(), configurable);
	}

	private DataEntry getEntry(String className, Configurable configurable) {
		return getRootEntry().getSingle("//"+className);
	}

	@Override
	public boolean save(File path) throws IOException {
		for (Configurable configurable : configurables.values()) {
			configurable.save(getEntry(configurable));
		}
		
		return super.save(path);
	}
}
