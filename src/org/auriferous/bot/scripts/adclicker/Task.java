package org.auriferous.bot.scripts.adclicker;

public class Task {
	public String url;
	public int timeOnAd;	
	public int shuffles;
	public int timeInterval;
	public int subClicks;
	
	public Task(String url, int shuffles, int timeInterval, int timeOnAd, int subClicks) {
		this.url = url;
		this.shuffles = shuffles;
		this.timeInterval = timeInterval;
		this.timeOnAd = timeOnAd;
		this.subClicks = subClicks;
	}
	
	public Task copy() {
		return new Task(url, shuffles, timeInterval, timeOnAd, subClicks);
	}
}
