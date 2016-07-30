package tech.conexus.webautomator.gui.swing;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import tech.conexus.webautomator.Utils;
import tech.conexus.webautomator.shared.tabs.view.PaintListener;

public class JOverlayComponent extends JComponent implements Runnable{
	private static final long serialVersionUID = 6573275233364499631L;

	private boolean running = false;
	
	private List<PaintListener> paintListeners = new LinkedList<PaintListener>();
	
	public JOverlayComponent() {
		start();
		new Thread(this).start();
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
			
			this.repaint(JConstants.UPDATE_INTERVAL);
			Utils.wait(JConstants.UPDATE_INTERVAL);
		}
	}
}
