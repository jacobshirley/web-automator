package org.auriferous.bot.script.input;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.auriferous.bot.tabs.view.TabPaintListener;
import org.auriferous.bot.tabs.view.TabView;

public class Mouse extends Input implements TabPaintListener {
	public Mouse(TabView target) {
		super(target);
	}
	
	public final void moveMouse(int x, int y) {
		target.dispatchMoveMouse(x, y);
	}
	
	public final void clickMouse(int x, int y, int button) {
		target.dispatchClickMouse(x, y, button);
	}
	
	public final void clickMouse(int x, int y) {
		clickMouse(x, y, MouseEvent.BUTTON1);
	}
	
	public final void scrollMouse(boolean up, int rotation) {
		target.dispatchScrollMouse(up, rotation);
	}
	
	public final int getScrollIncrement() {
		return 3;
	}
	
	public int getMouseX() {
		return target.getMouseX();
	}
	
	public int getMouseY() {
		return target.getMouseY();
	}
	
	public void setMousePos(int x, int y) {
		target.setMousePos(x, y);
	}
	
	public void setMousePos(Point p) {
		setMousePos(p.x, p.y);
	}

	@Override
	public void onPaint(Graphics g) {
		int mouseX = getMouseX();
		int mouseY = getMouseY();
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Color.green);
		g2d.setStroke(new BasicStroke(3));
		
		int mouseSize = 20;
		g2d.drawLine(mouseX-mouseSize, mouseY, mouseX+mouseSize, mouseY);
		g2d.drawLine(mouseX, mouseY-mouseSize, mouseX, mouseY+mouseSize);
	}
}
