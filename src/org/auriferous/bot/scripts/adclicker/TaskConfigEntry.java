package org.auriferous.bot.scripts.adclicker;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.bot.config.ConfigurableEntry;
import org.auriferous.bot.config.WritableEntry;

public class TaskConfigEntry extends WritableEntry<String,String>{
	private Task task;

	public TaskConfigEntry(Task t) {
		super("task");
		
		this.task = t;
	}
	
	@Override
	public List<ConfigurableEntry> getChildren() {
		List<ConfigurableEntry> attrs = new ArrayList<ConfigurableEntry>();
		
		attrs.add(new WritableEntry<String,String>("url", task.url));
		attrs.add(new WritableEntry<String,Integer>("shuffles", task.shuffles));
		attrs.add(new WritableEntry<String,Integer>("interval", task.timeInterval));
		attrs.add(new WritableEntry<String,Integer>("time-on-ad", task.timeOnAd));
		attrs.add(new WritableEntry<String,Integer>("sub-clicks", task.subClicks));
		attrs.add(new WritableEntry<String,String>("fb-link", task.fbLink));
		
		return attrs;
	}
}
