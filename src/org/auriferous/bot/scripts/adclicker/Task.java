package org.auriferous.bot.scripts.adclicker;

import org.auriferous.bot.config.ConfigurableEntry;

public class Task {
	public String url;
	public int timeOnAd;	
	public int shuffles;
	public int timeInterval;
	public int subClicks;
	
	public String fbLink;
	
	public Task(ConfigurableEntry entry) {
		load(entry);
	}
	
	public Task(String url, int shuffles, int timeInterval, int timeOnAd, int subClicks, String fbLink) {
		this.url = url;
		this.shuffles = shuffles;
		this.timeInterval = timeInterval;
		this.timeOnAd = timeOnAd;
		this.subClicks = subClicks;
		this.fbLink = fbLink;
	}
	
	public void load(ConfigurableEntry config) {
		this.url = ""+config.get("url", "");
		this.shuffles = Integer.parseInt(""+config.get("shuffles", "0"));
		this.timeInterval = Integer.parseInt(""+config.get("interval", "0"));
		this.timeOnAd = Integer.parseInt(""+config.get("time-on-ad", "0"));
		this.subClicks = Integer.parseInt(""+""+config.get("sub-clicks", "0"));
		
		this.fbLink = ""+""+config.get("fb-link", "");
	}
	
	public Task copy() {
		return new Task(url, shuffles, timeInterval, timeOnAd, subClicks, fbLink);
	}
}
