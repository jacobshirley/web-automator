package org.auriferous.webautomator.shared.data.history;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.webautomator.shared.data.DataEntry;

public class HistoryEntry extends DataEntry{
	private long timeStamp;
	private String faviconPath;
	private String title;
	private String url;
	
	public HistoryEntry(DataEntry entry) {
		this(Long.parseLong(entry.getValue("//time-stamp", 0).toString()), (String)entry.getValue("//favicon-path", ""), (String)entry.getValue("//title", ""), (String)entry.getValue("url", ""));
	}
	
	public HistoryEntry(String faviconPath, String title, String url) {
		this(System.currentTimeMillis(), faviconPath, title, url);
	}
	
	public HistoryEntry(long timeStamp, String faviconPath, String title, String url) {
		super("history-entry");
		
		this.timeStamp = timeStamp;
		this.faviconPath = faviconPath;
		this.title = title;
		this.url = url;
	}

	@Override
	public List<DataEntry> getChildren() {
		List<DataEntry> children = new ArrayList<DataEntry>();

		children.add(new DataEntry("time-stamp", timeStamp));
		children.add(new DataEntry("favicon-path", faviconPath));
		children.add(new DataEntry("title", title));
		children.add(new DataEntry("url", url));
		
		for (DataEntry e : super.getChildren())
			children.add(e);
		
		
		return children;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public String getFaviconPath() {
		return faviconPath;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getURL() {
		return url;
	}
	
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public void setFaviconPath(String faviconPath) {
		this.faviconPath = faviconPath;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
}
