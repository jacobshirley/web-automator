package org.adclicker.bot.tabs;

import java.awt.Graphics;

public interface TabListener {
	public void onTitleChange(Tab tab, String newTitle);
	
	public void onTabUpdating(Tab tab);
	
	public void onTabReloaded(Tab tab);
}
