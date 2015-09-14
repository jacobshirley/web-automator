package org.auriferous.bot.data.history;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.bot.data.DataEntry;
import org.auriferous.bot.data.config.Configurable;

public class HistoryConfig implements Configurable{
	private List<HistoryEntry> entries = new ArrayList<HistoryEntry>();
	
	public void addEntry(HistoryEntry entry) {
		entries.add(entry);
	}
	
	public void removeEntry(HistoryEntry entry) {
		entries.remove(entry);
	}
	
	public List<HistoryEntry> getEntries() {
		return entries;
	}

	@Override
	public void load(DataEntry configuration) {
		for (DataEntry entry : configuration.get("//history-entry")) {
			entries.add(new HistoryEntry(entry));
		}
	}

	@Override
	public void save(DataEntry configuration) {
		configuration.clear();
		for (DataEntry entry : entries) {
			configuration.add(entry);
		}
	}
}
