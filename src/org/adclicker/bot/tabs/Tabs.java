package org.adclicker.bot.tabs;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTabbedPane;

import org.adclicker.bot.Bot;

public class Tabs {
	private Bot bot;
	
	private LinkedList<Tab> tabs = new LinkedList<Tab>();

	private List<TabControlListener> tabListeners = new LinkedList<TabControlListener>();
	
	private JTabbedPane tabbedPane = new JTabbedPane();
	
	public Tabs(Bot bot) {
		this.bot = bot;
		
		this.bot.getFrame().add(tabbedPane);
	}
	
	public Tab openTab(String url) {
		Tab tab = new Tab(url);
		tab.addTabListener(new TabListener() {
			@Override
			public void onTitleChange(Tab tab, String newTitle) {
				System.out.println("TAB LOADED: "+newTitle);
				int index = tabbedPane.indexOfComponent(tab.getTabView());
				tabbedPane.setTitleAt(index, newTitle);
			}
			
			@Override
			public void onTabUpdating(Tab tab) {
			}
			
			@Override
			public void onTabReloaded(Tab tab) {
			}
		});
		
		tabbedPane.addTab("New Tab", tab.getTabView());
		tabs.add(tab);
		
		tab.id = tabs.indexOf(tab);
		
		for (TabControlListener listener : tabListeners) {
			listener.onTabAdded(tab);
		}
		
		return tab;
	}
	
	public Tab openTab() {
		return openTab("_blank");
	}
}
