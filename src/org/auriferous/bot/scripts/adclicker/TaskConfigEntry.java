package org.auriferous.bot.scripts.adclicker;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.bot.data.DataEntry;

public class TaskConfigEntry extends DataEntry{
	private Task task;

	public TaskConfigEntry(Task t) {
		super("task");
		
		this.task = t;
	}
	
	@Override
	public List<DataEntry> getChildren() {
		List<DataEntry> children = new ArrayList<DataEntry>();
		
		children.add(new DataEntry("url", task.url));
		children.add(new DataEntry("shuffles", task.shuffles));
		children.add(new DataEntry("interval", task.timeInterval));
		children.add(new DataEntry("time-on-ad", task.timeOnAd));
		children.add(new DataEntry("sub-clicks", task.subClicks));
		children.add(new DataEntry("fb-link", task.fbLink));
		
		return children;
	}
}
