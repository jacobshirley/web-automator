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

	public JTabBar(Tabs... scriptTabs) {
		this.scriptTabs = new ArrayList(Arrays.asList(scriptTabs));
		
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

	@Override
	public void onTabAdded(Tab tab) {
		addTab("New Tab", new JTab(this, tab));
	}

	@Override
	public void onTabClosed(Tab tab) {
		super.remove(getBarIndexByTab(tab));
	}

	@Override
	public void onTabUpdate(Tab tab) {
		
	}

	@Override
	public void onTabChange(Tab tab) {
		setSelectedIndex(getBarIndexByTab(tab));
	}
	
	public void addTab(JTab tab) {
		addTab(tab.getTab().getTitle(), tab);
	}
	
	public void addTab(String title, JTab tab) {
		int index = this.getTabCount();

		super.addTab(title, tab);
		setTabComponentAt(index, ((JTab)tab).getTabComponent());
		setSelectedIndex(index);
	}
	
	@Override
	public void remove(int index) {
		Component comp = getComponentAt(index);
		if (comp instanceof JTab) {
			Tab tab = ((JTab)comp).getTab();
			for (Tabs tabs : scriptTabs) {
				if (tabs.containsTab(tab)) {
					tabs.closeTab(tab);
				}
			}
		}
	}
	
	public int getBarIndexByTab(Tab tab) {
		for (int i = 0; i < getTabCount(); i++) {
			Component comp = getComponentAt(i);
			if (comp instanceof JTab) {
				Tab tab2 = ((JTab)comp).getTab();
				if (tab2.equals(tab))
					return i;
			}
		}
		return -1;
	}
	
	public Tab getTabByBarIndex(int index) {
		if (getTabCount() > 0) {
			Component comp = getComponentAt(index);
			if (comp instanceof JTab) {
				Tab tab = ((JTab)comp).getTab();
				return tab;
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