package org.auriferous.bot.gui.swing.tabs;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.view.TabPaintListener;
import org.auriferous.bot.tabs.view.TabView;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.DisposeEvent;
import com.teamdev.jxbrowser.chromium.events.DisposeListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class JTabView extends BrowserView implements DisposeListener<Browser>, TabView {
	private List<TabPaintListener> paintListeners = new LinkedList<TabPaintListener>();
	private long lastPainted = 0;
	private int mouseX;
	private int mouseY;
	
	public JTabView(Tab tab) {
		this(tab.getBrowserWindow());
	}
	
	public JTabView(Browser browser) {
		super(browser);
		
		browser.addDisposeListener(this);
	}
	
	//So it registers key events
	@Override
	public boolean isShowing() {
		return true;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		lastPainted = System.currentTimeMillis();
		
		for (TabPaintListener listener : paintListeners) 
			listener.onPaint(g);
	}
	
	@Override
	public void addTabPaintListener(TabPaintListener listener) {
		this.paintListeners.add(listener);
	}
	
	@Override
	public void removeTabPaintListener(TabPaintListener listener) {
		this.paintListeners.remove(listener);
	}
	
	public long getLastTimePainted() {
		return lastPainted;
	}

	@Override
	public void onDisposed(DisposeEvent<Browser> arg0) {
		this.paintListeners.clear();
	}
	
	@Override
	public final void dispatchMoveMouse(int x, int y) {
		this.mouseX = x;
		this.mouseY = y;
		
		MouseEvent event = new MouseEvent(this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 1, false);
		forwardMouseEvent(event);
	}
	
	@Override
	public final void dispatchClickMouse(int x, int y, int button) {
		MouseEvent event = new MouseEvent(this, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		//dispatchEvent(event);
		forwardMouseEvent(event);
		
		Utils.wait((int)(Utils.random(20, 50)));
		event = new MouseEvent(this, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		//dispatchEvent(event);
		forwardMouseEvent(event);
		
		event = new MouseEvent(this, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		//dispatchEvent(event);
		forwardMouseEvent(event);
	}
	
	@Override
	public final void dispatchScrollMouse(boolean up, int rotation) {
		MouseWheelEvent mwe = new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, mouseX, mouseY, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, up ? -rotation : rotation);
		//dispatchEvent(mwe);
		forwardMouseWheelEvent(mwe);
	}
	
	@Override
	public void dispatchTypeKey(int c, int time, int mods) {
		dispatchPressKey(c, mods);
		
		KeyEvent event = new KeyEvent(this, KeyEvent.KEY_TYPED, System.currentTimeMillis(), mods, KeyEvent.VK_UNDEFINED, (char)c, KeyEvent.KEY_LOCATION_UNKNOWN);
		forwardKeyTypedEvent(event);
	
		Utils.wait(time+Utils.random(20));
	
		dispatchReleaseKey(c, mods);
	}
	
	@Override
	public void dispatchPressKey(int c, int mods) {
		KeyEvent event = new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), mods, c, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
		forwardKeyPressedEvent(event);
	}
	
	@Override
	public void dispatchReleaseKey(int c, int mods) {
		KeyEvent event = new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), mods, c, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
		forwardKeyReleasedEvent(event);
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
