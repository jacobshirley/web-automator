package org.auriferous.bot.tabs;

public interface TabControlListener {
	public void onTabAdded(Tab tab);
	public void onTabRemoved(Tab tab);
	public void onTabUpdate(Tab tab);
	
	public void onChangeTab(Tab tab);
}
