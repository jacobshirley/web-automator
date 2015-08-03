package org.auriferous.bot.tabs;

import java.util.LinkedList;
import java.util.List;

import org.auriferous.bot.gui.tabs.TabView;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.TitleEvent;
import com.teamdev.jxbrowser.chromium.events.TitleListener;

public class Tab {
	private int id;
	private String originalURL;
	
	private Browser browser;
	private TabView tabView;
	
	private List<TabListener> tabListeners = new LinkedList<TabListener>();

	public Tab(int id, String url) {
		this.id = id;
		
		this.browser = new Browser();
		this.tabView = new TabView(browser);

		browser.addTitleListener(new TitleListener() {
            @Override
			public void onTitleChange(TitleEvent event) {
            	for (TabListener listener : tabListeners) 
					listener.onTitleChange(Tab.this, event.getTitle());
            }
        });
		
		loadURL(url);
	}
	
	public Tab(String url) {
		this(-1, url);
	}
	
	public void loadURL(String url) {
		this.originalURL = url;
		
		this.browser.loadURL(url);
		
		for (TabListener listener : tabListeners) 
			listener.onTabUpdating(this);
	}
	
	public void reload() {
		this.browser.reload();
		
		for (TabListener listener : tabListeners) 
			listener.onTabReloaded(this);
	}
	
	public String getTitle() {
		return browser.getTitle();
	}
	
	public String getOriginalURL() {
		return originalURL;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public Browser getBrowserWindow() {
		return browser;
	}
	
	public TabView getTabView() {
		return tabView;
	}
	
	public void addTabListener(TabListener tabListener) {
		this.tabListeners.add(tabListener);
	}
	
	public void removeTabListener(TabListener tabListener) {
		this.tabListeners.remove(tabListener);
	}
}
