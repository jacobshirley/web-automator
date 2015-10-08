package org.auriferous.bot.gui.swing.tabs;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.JBotFrame;
import org.auriferous.bot.gui.swing.JOverlayComponent;
import org.auriferous.bot.shared.tabs.Tab;
import org.auriferous.bot.shared.tabs.view.PaintListener;
import org.auriferous.bot.shared.tabs.view.TabView;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.DisposeEvent;
import com.teamdev.jxbrowser.chromium.events.DisposeListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class JTabView extends BrowserView implements DisposeListener<Browser>, TabView, PaintListener{
	private int mouseX;
	private int mouseY;
	
	private List<PaintListener> paintListeners = new LinkedList<PaintListener>();
	private JOverlayComponent paintableComponent;
	
	public JTabView(JOverlayComponent paintComponent, Tab tab) {
		this(paintComponent, tab.getBrowserInstance());
	}
	
	public JTabView(JOverlayComponent paintComponent, Browser browser) {
		super(browser);
		
		this.paintableComponent = paintComponent;
		browser.addDisposeListener(this);
		
		paintComponent.addPaintListener(this);
	}
	
	@Override
	public void onPaint(Graphics g) {
		if (super.isShowing()) {
			g.translate(this.getX(), this.getY()+50);
			for (PaintListener l : paintListeners) {
				l.onPaint(g);
			}
		}
	}
	
	//So it registers key events
	@Override
	public boolean isShowing() {
		return true;
	}
	
	@Override
	public void addPaintListener(PaintListener listener) {
		this.paintListeners.add(listener);
	}
	
	public List<PaintListener> getPaintListeners() {
		return paintListeners;
	}
	
	@Override
	public void removePaintListener(PaintListener listener) {
		this.paintListeners.remove(listener);
	}

	@Override
	public void onDisposed(DisposeEvent<Browser> arg0) {
		this.paintListeners.clear();
		paintableComponent.removePaintListener(this);
	}
	
	@Override
	public final void dispatchMoveMouse(int x, int y) {
		this.mouseX = x;
		this.mouseY = y;
		
		MouseEvent event = new MouseEvent(this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 1, false);
		super.forwardMouseEvent(event);
	}
	
	@Override
	public final void dispatchClickMouse(int x, int y, int button) {
		MouseEvent event = new MouseEvent(this, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		super.forwardMouseEvent(event);
		
		Utils.wait((Utils.random(20, 50)));
		event = new MouseEvent(this, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		super.forwardMouseEvent(event);
		
		event = new MouseEvent(this, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		super.forwardMouseEvent(event);
	}
	
	@Override
	public final void dispatchScrollMouse(boolean up, int rotation) {
		MouseWheelEvent mwe = new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, mouseX, mouseY, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, up ? -rotation : rotation);
		super.forwardMouseWheelEvent(mwe);
	}
	
	@Override
	public void dispatchTypeKey(int c, int time, int mods) {
		dispatchPressKey(c, mods);
		
		Utils.wait(time+Utils.random(20));
	
		dispatchReleaseKey(c, mods);
		
		KeyEvent event = new KeyEvent(this, KeyEvent.KEY_TYPED, System.currentTimeMillis(), mods, KeyEvent.VK_UNDEFINED, (char)c, KeyEvent.KEY_LOCATION_UNKNOWN);
		super.forwardKeyTypedEvent(event);
	}
	
	@Override
	public void dispatchPressKey(int c, int mods) {
		KeyEvent event = new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), mods, c, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
		super.forwardKeyPressedEvent(event);
	}
	
	@Override
	public void dispatchReleaseKey(int c, int mods) {
		KeyEvent event = new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), mods, c, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
		super.forwardKeyReleasedEvent(event);
	}
	
	@Override
	public void forwardMouseEvent(MouseEvent arg0) {
		if (!JBotFrame.mouseBlocked)
			super.forwardMouseEvent(arg0);
	}
	
	@Override
	public void forwardMouseWheelEvent(MouseWheelEvent arg0) {
		if (!JBotFrame.mouseBlocked)
			super.forwardMouseWheelEvent(arg0);
	}

	@Override
	public int getMouseX() {
		return mouseX;
	}

	@Override
	public int getMouseY() {
		return mouseY;
	}

	@Override
	public void setMousePos(int x, int y) {
		this.mouseX = x;
		this.mouseY = y;
	}
}
