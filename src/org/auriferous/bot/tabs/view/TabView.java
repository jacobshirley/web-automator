package org.auriferous.bot.tabs.view;

public interface TabView {
	public void addPaintListener(PaintListener listener);
	public void removePaintListener(PaintListener listener);
	
	public int getMouseX();
	public int getMouseY();
	
	public void setMousePos(int x, int y);
	
	public int getX();
	public int getY();
	
	public int getWidth();
	public int getHeight();
	
	public void dispatchMoveMouse(int x, int y);
	public void dispatchClickMouse(int x, int y, int button);
	public void dispatchScrollMouse(boolean up, int rotation);
	
	public void dispatchTypeKey(int c, int time, int mods);
	
	public void dispatchPressKey(int c, int mods);
	
	public void dispatchReleaseKey(int c, int mods);
}
