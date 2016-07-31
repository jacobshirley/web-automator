package tech.conexus.webautomator.shared.tabs;

import java.util.ArrayList;
import java.util.List;

import tech.conexus.webautomator.shared.data.history.HistoryConfig;

public class Tabs {
	private List<Tab> tabsList = new ArrayList<Tab>();

	private List<TabControlListener> tabListeners = new ArrayList<TabControlListener>();
	private int currentTabIndex = -1;

	private HistoryConfig history;

	public Tabs(HistoryConfig history) {
		this.history = history;
	}
	
	public Tab openTab(String url) {
		Tab tab = new Tab(this, url, history);
		tabsList.add(tab);

		tab.setID(tabsList.indexOf(tab));
		
		setCurrentTab(tab);
		for (TabControlListener listener : tabListeners) {
			listener.onTabAdded(tab);
			listener.onTabChange(tab);
		}
		
		return tab;
	}
	
	public Tab openTab() {
		return openTab("about:blank");
	}
	
	public Tab getCurrentTab() {
		if (tabsList.isEmpty()) 
			return null;
		
		return tabsList.get(currentTabIndex);
	}
	
	public boolean containsTab(Tab tab) {
		return this.tabsList.contains(tab);
	}
	
	public boolean hasTabs() {
		return !tabsList.isEmpty();
	}
	
	public void setCurrentTab(Tab tab) {
		setCurrentTab(tabsList.indexOf(tab));
	}
	
	public void setCurrentTab(int id) {
		if (id >= 0 && currentTabIndex != id) {
			currentTabIndex = id;
			if (currentTabIndex >= 0) {
				for (TabControlListener listener : tabListeners) {
					listener.onTabChange(tabsList.get(currentTabIndex));
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
		System.out.println("Closing tab "+tab);
		if (!tab.getBrowserInstance().isDisposed()) {
			tab.stop();
			tab.getBrowserInstance().dispose();
		}
		
		tab.handleClose();
		
		if (tabsList.contains(tab)) {
			tabsList.remove(tab);
			for (TabControlListener listener : tabListeners) {
				listener.onTabClosed(tab);
			}
		}
	}
	
	public void closeAll() {
		for (Tab tab : tabsList) {
			if (!tab.getBrowserInstance().isDisposed()) {
				tab.stop();
				tab.getBrowserInstance().dispose();
			}
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
}
