package org.auriferous.bot.gui.swing.tabs;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.auriferous.bot.Utils;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabControlListener;
import org.auriferous.bot.tabs.TabListener;
import org.auriferous.bot.tabs.Tabs;

public class JTabBar extends JTabbedPane implements TabControlListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private List<Tabs> scriptTabs;
	
	private List<JTab> tabList;
	
	public JTabBar(Tabs... scriptTabs) {
		this.scriptTabs = new ArrayList(Arrays.asList(scriptTabs));
		this.tabList = new ArrayList<JTab>();
		
		for (Tabs tabs : scriptTabs)
			tabs.addTabControlListener(this);
		
		addChangeListener(this);
	}
	
	public void addTabs(Tabs tabs) {
		this.scriptTabs.add(tabs);
		
		for (Tab tab : tabs.getTabList()) {
			addTab(new JTab(this, tab));
		}
		
		tabs.addTabControlListener(this);
	}
	
	public void removeTabs(Tabs tabs) {
		this.scriptTabs.remove(tabs);
		
		for (Tab tab : tabs.getTabList()) {
			remove(getBarIndexByTab(tab));
		}
		
		tabs.removeTabControlListener(this);
	}
	
	public void addTab(String title, JTab tab) {
		tabList.add(tab);
		setTabComponentAt(tabList.size()-1, tab.getTabComponent());
		
		addTab(title, tab.getTabView());
	}
	
	public void addTab(JTab tab) {
		addTab(tab.getTab().getTitle(), tab);
	}

	@Override
	public void onTabAdded(Tab tab) {
		addTab("New Tab", new JTab(this, tab));
	}

	@Override
	public void onTabClosed(Tab tab) {}

	@Override
	public void onTabUpdate(Tab tab) {}

	@Override
	public void onTabChange(Tab tab) {
		setSelectedIndex(getBarIndexByTab(tab));
	}
	
	@Override
	public void addTab(String arg0, Component arg1) {
		super.addTab(arg0, arg1);
		
		int index = this.getTabCount()-1;
		
		setSelectedIndex(index);
	}
	
	@Override
	public void remove(int index) {
		Tab tab = getTabByBarIndex(index);
		for (Tabs tabs : scriptTabs) {
			if (tabs.containsTab(tab)) {
				tabs.closeTab(tab);
			}
		}
		
		super.remove(index);
	}
	
	@Override
	public String getTitleAt(int index) {
		String s = super.getTitleAt(index);
		return s;
	}
	
	public int getBarIndexByTab(Tab tab) {
		int i = 0;
		for (JTab jTab : tabList) {
			if (jTab.getTab().equals(tab)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public Tab getTabByBarIndex(int index) {
		JTab tab = tabList.get(index);
		if (tab != null) {
			return tab.getTab();
		}
		return null;
	}
	
	@Override
	public void removeAll() {
		super.removeAll();
		
		for (Tabs tabs : scriptTabs) {
			tabs.closeAll();
		}
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		int index = getSelectedIndex();
		
		if (index >= 0) {
			Tab tab = getTabByBarIndex(index);
			for (Tabs tabs : scriptTabs) {
				if (tabs.containsTab(tab)) {
					tabs.setCurrentTab(tab);
				}
			}
			
		} else {
			for (Tabs tabs : scriptTabs) {
				tabs.setCurrentTab(-1);
			}
		}
	}
}