package org.auriferous.bot.scripts.adclicker;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.bot.config.ConfigurableEntry;

public class TaskConfigEntry extends ConfigurableEntry<String,String>{
	private Task task;

	public TaskConfigEntry(Task t) {
		super("task");
		
		this.task = t;
	}
	
	@Override
	public List<ConfigurableEntry> getChildren() {
		List<ConfigurableEntry> attrs = new ArrayList<ConfigurableEntry>();
		
		attrs.add(new ConfigurableEntry<String,String>("url", task.url));
		attrs.add(new ConfigurableEntry<String,Integer>("shuffles", task.shuffles));
		attrs.add(new ConfigurableEntry<String,Integer>("interval", task.timeInterval));
		attrs.add(new ConfigurableEntry<String,Integer>("time-on-ad", task.timeOnAd));
		attrs.add(new ConfigurableEntry<String,Integer>("sub-clicks", task.subClicks));
		
		return attrs;
	}
}
