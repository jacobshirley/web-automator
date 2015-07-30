package org.auriferous.bot.tabs;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class TabView extends BrowserView {
	private List<TabPaintListener> paintListeners = new LinkedList<TabPaintListener>();
	
	public TabView(Browser browser) {
		super(browser);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		for (TabPaintListener listener : paintListeners) 
			listener.onPaint(g);
	}
	
	public void addTabPaintListener(TabPaintListener listener) {
		this.paintListeners.add(listener);
	}
	
	public void removeTabPaintListener(TabPaintListener listener) {
		this.paintListeners.remove(listener);
	}
}
