package org.auriferous.bot.tabs;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JTabbedPane;

import org.auriferous.bot.gui.Bot;

public class Tabs {
	private Bot bot;
	
	private LinkedList<Tab> tabs = new LinkedList<Tab>();

	private List<TabControlListener> tabListeners = new LinkedList<TabControlListener>();
	
	private int currentTab;

	public Tabs() {
	}
	
	public synchronized Tab openTab(String url) {
		Tab tab = new Tab(url);
		tabs.add(tab);
		
		tab.setID(tabs.indexOf(tab));
		
		for (TabControlListener listener : tabListeners) {
			listener.onTabAdded(tab);
		}
		
		return tab;
	}
	
	public Tab openTab() {
		return openTab("about:blank");
	}
	
	public Tab getCurrentTab() {
		return tabs.get(currentTab);
	}
	
	public Tab getTab(int index) {
		return tabs.get(index);
	}
	
	public List<Tab> getTabList() {
		return tabs;
	}
	
	public void addTabControlListener(TabControlListener tcL) {
		this.tabListeners.add(tcL);
	}
	
	public void removeTabControlListener(TabControlListener tcL) {
		this.tabListeners.remove(tcL);
	}
}
