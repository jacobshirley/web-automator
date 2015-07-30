package org.auriferous.bot.tabs.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.auriferous.bot.Utils;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabControlListener;
import org.auriferous.bot.tabs.TabListener;
import org.auriferous.bot.tabs.Tabs;

public class TabBar extends JTabbedPane implements TabListener, TabControlListener {
	private static final long serialVersionUID = 1L;
	private Tabs tabs;

	public TabBar(Tabs tabs) {
		this.tabs = tabs;
		this.tabs.addTabControlListener(this);
	}

	@Override
	public void onTitleChange(Tab tab, String newTitle) {
		int id = this.indexOfComponent(tab.getTabView());
		setTitleAt(id, newTitle);
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
	public void onTabRemoved(Tab tab) {}

	@Override
	public void onTabUpdate(Tab tab) {}
}
