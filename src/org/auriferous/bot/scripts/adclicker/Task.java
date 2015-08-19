package org.auriferous.bot.scripts.adclicker;

import java.util.List;

import org.auriferous.bot.config.ConfigurableEntry;

public class Task {
	public String url;
	public int timeOnAd;	
	public int shuffles;
	public int timeInterval;
	public int subClicks;
	
	public Task(ConfigurableEntry entry) {
		load(entry);
	}
	
	public Task(String url, int shuffles, int timeInterval, int timeOnAd, int subClicks) {
		this.url = url;
		this.shuffles = shuffles;
		this.timeInterval = timeInterval;
		this.timeOnAd = timeOnAd;
		this.subClicks = subClicks;
	}
	
	public void load(ConfigurableEntry config) {
		List<ConfigurableEntry> entries = config.getChildren();
		
		this.url = (String) entries.get(0).getValue();
		this.shuffles = Integer.parseInt(""+entries.get(1).getValue());
		this.timeInterval = Integer.parseInt(""+entries.get(2).getValue());
		this.timeOnAd = Integer.parseInt(""+entries.get(3).getValue());
		this.subClicks = Integer.parseInt(""+entries.get(4).getValue());
	}
	
	public Task copy() {
		return new Task(url, shuffles, timeInterval, timeOnAd, subClicks);
	}
}
