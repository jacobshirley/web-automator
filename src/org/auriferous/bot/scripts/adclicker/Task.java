package org.auriferous.bot.scripts.adclicker;

import org.auriferous.bot.shared.data.DataEntry;

public class Task {
	public String url;
	public int timeOnAd;	
	public int shuffles;
	public int timeInterval;
	public int subClicks;
	
	public String fbLink;
	
	public Task(DataEntry entry) {
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
	
	public void load(DataEntry config) {
		//System.out.println(config.getValue("shuffles", ""));
		
		this.url = ""+config.getValue("url", "");
		this.shuffles = Integer.parseInt(""+config.getValue("shuffles", "0"));
		this.timeInterval = Integer.parseInt(""+config.getValue("interval", "0"));
		this.timeOnAd = Integer.parseInt(""+config.getValue("time-on-ad", "0"));
		this.subClicks = Integer.parseInt(""+""+config.getValue("sub-clicks", "0"));
		this.fbLink = ""+""+config.getValue("fb-link", "");
	}
	
	public Task copy() {
		return new Task(url, shuffles, timeInterval, timeOnAd, subClicks, fbLink);
	}
}
