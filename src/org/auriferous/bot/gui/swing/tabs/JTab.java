package org.auriferous.bot.gui.swing.tabs;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabListener;
import org.auriferous.bot.tabs.view.TabView;

public class JTab extends JPanel implements TabListener {
	private JTabComponent tabComponent;
	private Tab tab;
	private JTabBar tabBar;
	private JTabView tabView;
	
	public JTab(JTabBar tabBar, Tab tab) {
		super(new BorderLayout());
		
		this.tabBar = tabBar;
		this.tabComponent = new JTabComponent(tabBar);
		
		this.tab = tab;
		this.tab.addTabListener(this);
		
		this.tabView = new JTabView(this.tab);
		this.tab.setTabView(tabView);
		
		this.add(tabView);
	}
	
	public JTabComponent getTabComponent() {
		return tabComponent;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public JTabView getJTabView() {
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
