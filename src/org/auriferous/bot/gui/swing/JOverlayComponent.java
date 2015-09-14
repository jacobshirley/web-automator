package org.auriferous.bot.gui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.tabs.JTab;
import org.auriferous.bot.gui.swing.tabs.JTabView;
import org.auriferous.bot.tabs.view.PaintListener;

public class JOverlayComponent extends JComponent implements Runnable{
	private boolean running = false;
	
	private List<PaintListener> paintListeners = new LinkedList<PaintListener>();
	
	public JOverlayComponent() {
		start();
		//new Thread(this).start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (PaintListener listener : paintListeners) 
			listener.onPaint(g);
	}
	
	public void addPaintListener(PaintListener listener) {
		this.paintListeners.add(listener);
	}
	
	public void removePaintListener(PaintListener listener) {
		this.paintListeners.remove(listener);
	}
	
	public void start() {
		this.running = true;
	}
	
	public void stop() {
		this.running = false;
	}
	@Override
	public void run() {
		while (running) {
			this.repaint(Constants.UPDATE_INTERVAL);
			Utils.wait(Constants.UPDATE_INTERVAL);
		}
	}
}
