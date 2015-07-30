package org.adclicker.bot.tabs;

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
}
