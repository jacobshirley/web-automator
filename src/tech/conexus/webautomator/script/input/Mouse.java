package tech.conexus.webautomator.script.input;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import tech.conexus.webautomator.shared.tabs.Tab;
import tech.conexus.webautomator.shared.tabs.view.PaintListener;
import tech.conexus.webautomator.shared.tabs.view.TabView;

public class Mouse extends Input implements PaintListener {
	public Mouse(Tab target) {
		super(target);
	}
	
	public final void moveMouse(int x, int y) {
		target.getTabView().dispatchMoveMouse(x, y);
	}
	
	public final void clickMouse(int x, int y, int button) {
		target.getTabView().dispatchClickMouse(x, y, button);
	}
	
	public final void clickMouse(int x, int y) {
		clickMouse(x, y, MouseEvent.BUTTON1);
	}
	
	public final void scrollMouse(boolean up, int rotation) {
		target.getTabView().dispatchScrollMouse(up, rotation);
	}
	
	public final int getScrollIncrement() {
		return 3;
	}
	
	public int getMouseX() {
		return target.getTabView().getMouseX();
	}
	
	public int getMouseY() {
		return target.getTabView().getMouseY();
	}
	
	public void setMousePos(int x, int y) {
		target.getTabView().setMousePos(x, y);
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
