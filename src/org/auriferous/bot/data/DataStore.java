package org.auriferous.bot.data;

import java.io.File;
import java.io.IOException;

public abstract class DataStore {
	protected File file;
	protected RootEntry rootEntry;
	
	public DataStore(RootEntry rootEntry) {
		this.rootEntry = rootEntry;
	}
	
	public DataStore(File file) {
		this.file = file;
	}
	
	public DataEntry getRootEntry() {
		return rootEntry;
	}
	
	public void setRootEntry(RootEntry rootEntry) {
		this.rootEntry = rootEntry;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	
	public boolean save() throws IOException {
		if (file != null) {
			return save(file);
		} else throw new IOException("Please make sure you have set an output path. Use setFile.");
	}
	
	public abstract boolean save(File file) throws IOException;
}
