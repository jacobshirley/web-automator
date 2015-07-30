package org.auriferous.bot.tabs.gui;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.auriferous.bot.Utils;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabControlListener;
import org.auriferous.bot.tabs.TabListener;
import org.auriferous.bot.tabs.TabView;
import org.auriferous.bot.tabs.Tabs;

public class TabBar extends JTabbedPane implements TabListener, TabControlListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private Tabs tabs;
	
	private static final int REFRESH_RATE = 10;
	private static final int UPDATE_INTERVAL = 1000/REFRESH_RATE;

	public TabBar(Tabs tabs) {
		this.tabs = tabs;
		this.tabs.addTabControlListener(this);
		
		addChangeListener(this);
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (TabBar.this.tabs.hasTabs()) {
						Tab cur = TabBar.this.tabs.getCurrentTab();
						TabView view = cur.getTabView();
						
						if (System.currentTimeMillis() - view.lastPainted >= UPDATE_INTERVAL) {
							//System.out.println("Repainting");
							view.repaint();
						}
						Utils.wait(UPDATE_INTERVAL);
					}
					Thread.yield();
				}
			}
		}).start();
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
		
		setSelectedIndex(this.tabs.getTabList().size()-1);
	}

	@Override
	public void onTabRemoved(Tab tab) {}

	@Override
	public void onTabUpdate(Tab tab) {}

	@Override
	public void onChangeTab(Tab tab) {
		
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		int index = getSelectedIndex();
		for (Tab tab : tabs.getTabList()) {
			if (tab.getTabView().equals(getComponentAt(index))) {
				tabs.setCurrentTab(tab.getID());
				break;
			}
		}
	}
}
