package org.adclicker.input;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.adclicker.bot.PaintListener;
import org.adclicker.bot.utils.Utils;

public class MouseSimulator implements PaintListener {
	private Component target;
	
	private int mouseX;
	private int mouseY;
	
	private int width;
	private int height;
	
	public MouseSimulator(Component target, int width, int height) {
		this.target = target;
		
		this.width = width;
		this.height = height;
		
		this.mouseX = this.width/2;
		this.mouseY = this.height/2;
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
	}
	
	public final void clickMouse(int x, int y) {
		clickMouse(x, y, MouseEvent.BUTTON1);
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
		g.setColor(Color.red);
		g.drawLine(mouseX-10, mouseY, mouseX+10, mouseY);
		g.drawLine(mouseX, mouseY-10, mouseX, mouseY+10);
	}
}
