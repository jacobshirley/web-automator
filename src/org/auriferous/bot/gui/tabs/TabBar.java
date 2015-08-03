package org.auriferous.bot.gui.tabs;

import java.awt.Component;

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
	private Tabs tabs;
	public TabBar(Tabs tabs) {
		this.tabs = tabs;
		this.tabs.addTabControlListener(this);
		
		addChangeListener(this);
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
		
		setSelectedIndex(this.tabs.getTabList().size()-1);
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
		
		int index = this.tabs.getTabList().size()-1;
		setTabComponentAt(index, new TabComponent(this));
	}
	
	@Override
	public void remove(int index) {
		Tab tab = getTabByBarIndex(index);
		if (tab != null)
			this.tabs.closeTab(tab);
		
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
		for (Tab tab : tabs.getTabList()) {
			if (tab.getTabView().equals(getComponentAt(index))) {
				return tab;
			}
		}
		return null;
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		int index = getSelectedIndex();
		
		if (index >= 0) {
			tabs.setCurrentTab(getTabByBarIndex(index));
		} else {
			tabs.setCurrentTab(-1);
		}
	}
}
