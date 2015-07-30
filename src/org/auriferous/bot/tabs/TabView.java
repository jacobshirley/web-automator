package org.auriferous.bot.tabs;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class TabView extends BrowserView {
	private List<TabPaintListener> paintListeners = new LinkedList<TabPaintListener>();
	private int c;
	private BrowserView browserView;
	public long lastPainted = 0;
	
	public TabView(Browser browser) {
		super(browser);
		//this.browserView = new BrowserView(browser);

		//JLayeredPane pane = new JLayeredPane();
		//pane.setDoubleBuffered(true);
		//pane.add(this.browserView, BorderLayout.CENTER);
		
		//add(browserView);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		for (TabPaintListener listener : paintListeners) 
			listener.onPaint(g);
		
		lastPainted = System.currentTimeMillis();
	}
	
	public void addTabPaintListener(TabPaintListener listener) {
		this.paintListeners.add(listener);
	}
	
	public void removeTabPaintListener(TabPaintListener listener) {
		this.paintListeners.remove(listener);
	}
}
