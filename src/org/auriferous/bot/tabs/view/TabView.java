package org.auriferous.bot.tabs.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ComponentListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.auriferous.bot.tabs.Tab;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class TabView extends BrowserView {
	private List<TabPaintListener> paintListeners = new LinkedList<TabPaintListener>();
	private long lastPainted = 0;
	
	public TabView(Tab tab) {
		this(tab.getBrowserWindow());
	}
	
	public TabView(Browser browser) {
		super(browser);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		lastPainted = System.currentTimeMillis();
		
		for (TabPaintListener listener : paintListeners) 
			listener.onPaint(g);
	}
	
	public void addTabPaintListener(TabPaintListener listener) {
		this.paintListeners.add(listener);
	}
	
	public void removeTabPaintListener(TabPaintListener listener) {
		this.paintListeners.remove(listener);
	}
	
	public long getLastTimePainted() {
		return lastPainted;
	}
}
