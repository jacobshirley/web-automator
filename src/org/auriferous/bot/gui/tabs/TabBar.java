package org.auriferous.bot.gui.tabs;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.auriferous.bot.Utils;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabControlListener;
import org.auriferous.bot.tabs.TabListener;
import org.auriferous.bot.tabs.Tabs;

public class TabBar extends JTabbedPane implements TabListener, TabControlListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private List<Tabs> scriptTabs;
	public TabBar(Tabs... scriptTabs) {
		this.scriptTabs = new ArrayList(Arrays.asList(scriptTabs));
		
		for (Tabs tabs : scriptTabs)
			tabs.addTabControlListener(this);
		
		addChangeListener(this);
	}
	
	public void addTabs(Tabs tabs) {
		this.scriptTabs.add(tabs);
		
		for (Tab tab : tabs.getTabList()) {
			tab.addTabListener(this);
			addTab(tab.getTitle(), tab.getTabView());
		}
		
		tabs.addTabControlListener(this);
	}
	
	public void removeTabs(Tabs tabs) {
		this.scriptTabs.remove(tabs);
		
		for (Tab tab : tabs.getTabList()) {
			
			tab.removeTabListener(this);
			remove(getBarIndexByTab(tab));
		}
		
		tabs.removeTabControlListener(this);
	}

	@Override
	public void onTitleChange(Tab tab, String newTitle) {
		int id = this.indexOfComponent(tab.getTabView());
		
		setTitleAt(id, newTitle);
		getTabComponentAt(id).revalidate();
	}

	@Override
	public void onTabUpdating(Tab tab) {}

	@Override
	public void onTabReloaded(Tab tab) {}

	@Override
	public void onTabAdded(Tab tab) {
		tab.addTabListener(this);
		addTab("New Tab", tab.getTabView());
	}

	@Override
	public void onTabClosed(Tab tab) {}

	@Override
	public void onTabUpdate(Tab tab) {}

	@Override
	public void onChangeTab(Tab tab) {
		setSelectedIndex(getBarIndexByTab(tab));
	}
	
	@Override
	public void addTab(String arg0, Component arg1) {
		super.addTab(arg0, arg1);
		
		int index = this.getTabCount()-1;
		setTabComponentAt(index, new TabComponent(this));
		
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
	
	private int getBarIndexByTab(Tab tab) {
		for (int i = 0; i < getTabCount(); i++) {
			if (tab.getTabView().equals(getComponentAt(i))) {
				return i;
			}
		}
		return -1;
	}
	
	private Tab getTabByBarIndex(int index) {
		for (Tabs tabs : scriptTabs) {
			for (Tab tab : tabs.getTabList()) {
				if (tab.getTabView().equals(getComponentAt(index))) {
					return tab;
				}
			}
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
				if (tabs.containsTab(tab)) 
					tabs.setCurrentTab(tab);
			}
			
		} else {
			for (Tabs tabs : scriptTabs) {
				tabs.setCurrentTab(-1);
			}
		}
	}
}
