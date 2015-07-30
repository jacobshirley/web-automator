package org.auriferous.bot.tabs;

public interface TabListener {
	public void onTitleChange(Tab tab, String newTitle);
	
	public void onTabUpdating(Tab tab);
	
	public void onTabReloaded(Tab tab);
}
