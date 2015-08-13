package org.auriferous.bot.gui.swing.tabs;

import javax.swing.JPanel;

import org.auriferous.bot.gui.tabs.TabView;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabListener;

public class JTab extends JPanel implements TabListener {
	private TabView tabView;
	private JTabComponent tabComponent;
	private Tab tab;
	private JTabBar tabBar;
	
	public JTab(JTabBar tabBar, Tab tab) {
		this.tabBar = tabBar;
		this.tabComponent = new JTabComponent(tabBar);
		this.tabView = new TabView(tab);
		
		this.tab = tab;
		this.tab.addTabListener(this);
		
		this.add(tabView);
	}
	
	public JTabComponent getTabComponent() {
		return tabComponent;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public TabView getTabView() {
		return tabView;
	}

	@Override
	public void onTitleChange(String newTitle) {
		int id = tabBar.indexOfComponent(this);
		
		tabBar.setTitleAt(id, newTitle);
		tabBar.getTabComponentAt(id).revalidate();
	}

	@Override
	public void onTabUpdating() {
	}

	@Override
	public void onTabReloaded() {
	}
}
