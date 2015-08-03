package org.auriferous.bot.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.auriferous.bot.script.Script;

public class Tabs {
	private Map<Script, List<Tab>> scriptTabMap = new HashMap<Script, List<Tab>>();
	
	private List<Tab> tabsList = new ArrayList<Tab>();

	private List<TabControlListener> tabListeners = new ArrayList<TabControlListener>();
	private int currentTabIndex = 0;

	public Tabs() {
	}
	
	public Tab openTab(String url, Script script) {
		Tab newTab = this.openTab(url);
		
		List<Tab> tabsOfScript = scriptTabMap.get(script);
		if (tabsOfScript == null) {
			tabsOfScript = new LinkedList<Tab>();
			scriptTabMap.put(script, tabsOfScript);
		}
		tabsOfScript.add(newTab);
		
		return newTab;
	}
	
	public Tab openTab(Script script) {
		return openTab("about:blank", script);
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
	
	public List<Tab> getTabsByScript(Script script) {
		return scriptTabMap.get(script);
	}
	
	public void closeTab(int id) {
		closeTab(tabsList.get(id));
	}
	
	public void closeTab(Tab tab) {
		tabsList.remove(tab);
		for (TabControlListener listener : tabListeners) {
			listener.onTabClosed(tab);
		}
	}
	
	public void closeTabsByScript(Script script) {
		for (Tab tab : scriptTabMap.get(script)) {
			closeTab(tab);
		}
	}
	
	public void addTabControlListener(TabControlListener tcL) {
		this.tabListeners.add(tcL);
	}
	
	public void removeTabControlListener(TabControlListener tcL) {
		this.tabListeners.remove(tcL);
	}
}
