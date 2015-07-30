package org.adclicker.bot.tabs;

import java.util.LinkedList;
import java.util.List;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.TitleEvent;
import com.teamdev.jxbrowser.chromium.events.TitleListener;

public class Tab {
	public int id;
	public String title = "Untitled";
	public String url;
	
	private Browser browser;
	private TabView tabView;
	
	private List<TabListener> tabListeners = new LinkedList<TabListener>();

	public Tab(int id, String url) {
		this.id = id;
		
		this.browser = new Browser();
		this.tabView = new TabView(browser);

		browser.addTitleListener(new TitleListener() {
            public void onTitleChange(TitleEvent event) {
            	for (TabListener listener : tabListeners) 
					listener.onTitleChange(Tab.this, event.getTitle());
            }
        });
		
		this.browser.loadURL(url);
	}
	
	public Tab(String url) {
		this(-1, url);
	}
	
	public void loadURL(String url) {
		this.browser.loadURL(url);
		
		for (TabListener listener : tabListeners) 
			listener.onTabUpdating(this);
	}
	
	public void reload() {
		this.browser.reload();
		
		for (TabListener listener : tabListeners) 
			listener.onTabReloaded(this);
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
