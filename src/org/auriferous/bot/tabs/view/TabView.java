package org.auriferous.bot.tabs.view;

import java.awt.event.KeyEvent;

import org.auriferous.bot.Utils;

public interface TabView {
	public void addTabPaintListener(TabPaintListener listener);
	public void removeTabPaintListener(TabPaintListener listener);
	
	public void dispatchMoveMouse(int x, int y);
	public void dispatchClickMouse(int x, int y, int button);
	public void dispatchScrollMouse(boolean up, int rotation);
	
	public void dispatchTypeKey(int c, int time, int mods);
	
	public void dispatchPressKey(int c);
	
	public void dispatchReleaseKey(int c);
}
