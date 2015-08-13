package org.auriferous.bot.gui.swing.script;

import javax.swing.JMenu;

import org.auriferous.bot.gui.tabs.TabView;
import org.auriferous.bot.tabs.Tab;

public interface JGuiListener {
	public void onJMenuCreated(JMenu menu);
	
	public void onTabViewCreated(Tab tab, TabView tabView);
}
