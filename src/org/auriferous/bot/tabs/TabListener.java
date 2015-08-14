package org.auriferous.bot.tabs;

public interface TabListener {
	public void onTitleChange(String newTitle);
	
	public void onTabUpdating();
	
	public void onTabReloaded();
}
