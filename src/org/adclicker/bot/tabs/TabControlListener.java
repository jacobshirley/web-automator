package org.adclicker.bot.tabs;

import java.awt.Graphics;

public interface TabControlListener {
	public void onTabAdded(Tab tab);
	public void onTabRemoved(Tab tab);
	public void onTabUpdate(Tab tab);
}
