package org.auriferous.bot.shared.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jxpath.DynamicPropertyHandler;

public class DataEntryHandler implements DynamicPropertyHandler{
	
	
	@Override
	public Object getProperty(Object object, String property) {
		DataEntry entry = (DataEntry)object;
		
		if (property.equals("children")) {
			return entry.getChildren();
		} else if (property.equals("text") || property.equals("value")) {
			return entry.getValue();
		} else {
			List<DataEntry> results = new ArrayList<DataEntry>();
			
			for (DataEntry e : entry.getChildren()) {
				if (e.getKey().equals(property)) {
					results.add(e);
				}
			}
			
			return results;
		}
	}

	@Override
	public String[] getPropertyNames(Object object) {
		DataEntry entry = (DataEntry)object;
		List<DataEntry> children = entry.getChildren();
		String[] names = new String[children.size()+2];
		
		names[0] = "text";
		names[1] = "value";
		
		for (int i = 2; i < names.length; i++) {
			names[i] = ""+children.get(i-2).getKey();
		}
		
		return names;
	}

	@Override
	public void setProperty(Object object, String property, Object value) {
		DataEntry entry = (DataEntry)object;
		
		for (DataEntry e : entry.getChildren()) {
			if (e.getKey().equals(property)) {
				e.setValue(value);
			}
		}
	}
}
