package org.auriferous.bot.data.config;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.auriferous.bot.data.DataEntry;
import org.auriferous.bot.data.DataStore;
import org.auriferous.bot.data.RootEntry;
import org.auriferous.bot.data.xml.XMLDataStore;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
