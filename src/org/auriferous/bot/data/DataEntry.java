package org.auriferous.bot.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.Pointer;

public class DataEntry {
	static {
		JXPathIntrospector.registerDynamicClass(DataEntry.class, DataEntryHandler.class);
	}
	
	private List<DataEntry> parents = new LinkedList<DataEntry>();
	
	private Object key;
	private Object value;
	
	private List<DataEntry> children = new ArrayList<DataEntry>();
	
	private JXPathContext context;

	public DataEntry(Object key) {
		this(key, null);
	}
	
	public DataEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
		
	}
	
	private void createContext() {
		if (this.context == null) {
			this.context = JXPathContext.newContext(this);
			this.context.setLenient(true);
		}
	}

	public Object getKey() {
		return key;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setKey(Object key) {
		this.key = key;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public void clear() {
		children.clear();
	}
	
	public void add(String xpath, DataEntry subEntry) {
		add(xpath, subEntry, false);
	}
	
	public void add(String xpath, DataEntry subEntry, boolean overwrite) {
		createContext();
		
		Iterator<DataEntry> it = context.iterate(xpath);
		
		while (it.hasNext()) {
			DataEntry e = it.next();

			e.add(subEntry, overwrite);
		}
	}
	
	public void add(DataEntry subEntry) {
		add(subEntry, false);
	}
	
	public void add(DataEntry subEntry, boolean overwrite) {
		boolean write = true;
		if (children.contains(subEntry)) {
			write = !overwrite;
		}
		if (write) {
			subEntry.parents.add(this);
			children.add(subEntry);
		}
	}
	
	public void set(String xpath, Object value) {
		context.setValue(xpath, value);
	}
	
	public boolean remove(DataEntry subEntry) {
		subEntry.parents.remove(this);
		return children.remove(subEntry);
	}
	
	public void remove(String xpath) {
		createContext();
		
		Iterator<DataEntry> it = context.iterate(xpath);
		
		while (it.hasNext()) {
			DataEntry e = it.next();

			for (DataEntry parent : e.parents) {
				parent.remove(e);
			}
		}
	}
	
	public List<DataEntry> get(String xpath) {
		createContext();
		
		return (List<DataEntry>)context.selectNodes(xpath);
	}
	
	public DataEntry getSingle(String xpath) {
		List<DataEntry> g = get(xpath);
		return g.isEmpty() ? null : g.get(0);
	}
	
	public Object getValue(String xpath, Object defaultValue) {
		DataEntry gS = getSingle(xpath);
		return gS == null ? defaultValue : gS.getValue();
	}
	
	public boolean contains(String xpath) {
		return getSingle(xpath) != null;
	}
	
	public List<DataEntry> getChildren() {
		return children;
	}
	
	@Override
	public String toString() {
		return "Key: "+key + ", Value: "+value;
	}
}