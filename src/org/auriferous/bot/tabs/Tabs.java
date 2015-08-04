package org.auriferous.bot.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.auriferous.bot.script.Script;

public class Tabs {
	private static final List<Tabs> TABS_INSTANCES = new ArrayList<Tabs>();
	
	private List<Tab> tabsList = new ArrayList<Tab>();

	private List<TabControlListener> tabListeners = new ArrayList<TabControlListener>();
	private int currentTabIndex = 0;

	public Tabs() {
	}
	
	
	public Tab openTab(String url) {
		Tab tab = new Tab(url);
		tabsList.add(tab);

		tab.setID(tabsList.indexOf(tab));

		for (TabControlListener listener : tabListeners) {
			listener.onTabAdded(tab);
		}
		
		return tab;
	}
	
	public Tab openTab() {
		return openTab("about:blank");
	}
	
	public Tab getCurrentTab() {
		return tabsList.get(currentTabIndex);
	}
	
	public boolean containsTab(Tab tab) {
		return this.tabsList.contains(tab);
	}
	
	public boolean hasTabs() {
		return !tabsList.isEmpty();
	}
	
	public void setCurrentTab(Tab tab) {
		if (tab != null) {
			currentTabIndex = tabsList.indexOf(tab);
		}
	}
	
	public void setCurrentTab(int id) {
		if (currentTabIndex != id) {
			currentTabIndex = id;
			if (currentTabIndex >= 0) {
				for (TabControlListener listener : tabListeners) {
					listener.onChangeTab(tabsList.get(currentTabIndex));
				}
			}
		}
	}
	
	public Tab getTab(int index) {
		return tabsList.get(index);
	}
	
	public List<Tab> getTabList() {
		return tabsList;
	}
	
	public void closeTab(int id) {
		Tab tab = tabsList.get(id);
		
		closeTab(tab);
	}
	
	public void closeTab(Tab tab) {
		tab.getBrowserWindow().dispose();
		tabsList.remove(tab);
		for (TabControlListener listener : tabListeners) {
			listener.onTabClosed(tab);
		}
	}
	
	public void closeAll() {
		for (Tab tab : tabsList) {
			tab.getBrowserWindow().dispose();
			for (TabControlListener listener : tabListeners) {
				listener.onTabClosed(tab);
			}
		}
		tabsList.clear();
	}
	
	public void addTabControlListener(TabControlListener tcL) {
		this.tabListeners.add(tcL);
	}
	
	public void removeTabControlListener(TabControlListener tcL) {
		this.tabListeners.remove(tcL);
	}
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	for (Tabs tabs : TABS_INSTANCES)
		    		tabs.closeAll();
		    }
		}));
	}
}
