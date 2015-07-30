package org.adclicker.input;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.adclicker.bot.tabs.TabControlListener;
import org.adclicker.bot.tabs.TabPaintListener;
import org.adclicker.bot.utils.Utils;

public class MouseSimulator implements TabPaintListener {
	private Component target;
	
	private int mouseX;
	private int mouseY;
	
	public MouseSimulator(Component target) {
		this.target = target;
	}
	
	public final void moveMouse(int x, int y) {
		this.mouseX = x;
		this.mouseY = y;
		
		MouseEvent event = new MouseEvent(target, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 1, false);
		target.dispatchEvent(event);
	}
	
	public final void clickMouse(int x, int y, int button) {
		MouseEvent event = new MouseEvent(target, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		target.dispatchEvent(event);
		
		Utils.wait((int)(Utils.randomRange(20, 50)));
		event = new MouseEvent(target, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		target.dispatchEvent(event);
		
		event = new MouseEvent(target, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false, button);
		target.dispatchEvent(event);
	}
	
	public final void clickMouse(int x, int y) {
		clickMouse(x, y, MouseEvent.BUTTON1);
	}
	
	public final void scrollMouse(boolean up, int rotation) {
		MouseWheelEvent mwe = new MouseWheelEvent(target, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, mouseX, mouseY, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, up ? -rotation : rotation);
		target.dispatchEvent(mwe);
	}
	
	public final int getScrollIncrement() {
		return 3;
	}
	
	public int getMouseX() {
		return mouseX;
	}
	
	public int getMouseY() {
		return mouseY;
	}
	
	public void setMousePos(int x, int y) {
		this.mouseX = x;
		this.mouseY = y;
	}
	
	public void setMousePos(Point p) {
		setMousePos(p.x, p.y);
	}

	@Override
	public void onPaint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Color.green);
		g2d.setStroke(new BasicStroke(3));
		g2d.drawLine(mouseX-10, mouseY, mouseX+10, mouseY);
		g2d.drawLine(mouseX, mouseY-10, mouseX, mouseY+10);
	}
}
