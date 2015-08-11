package org.auriferous.bot.script;

import java.awt.Point;
import java.awt.Rectangle;

import org.auriferous.bot.Utils;

public class ElementBounds extends Rectangle{
	private ElementBounds iframe;
	
	public ElementBounds(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public Point getRandomPoint() {
		return new Point((int)(x+Utils.random(width)), (int)(y+Utils.random(height)));
	}
	
	public Point getRandomPointFromCentre(double percX, double percY) {
		double w = width/2;
		double h = height/2;
		
		int xx = (int) (getCenterX()+(w*Utils.randomRange(-percX, percX)));
		int yy = (int) (getCenterY()+(h*Utils.randomRange(-percY, percY)));
		
		return new Point(xx, yy);
	}
	
	public void appendParent(ElementBounds parent) {
		this.x += parent.x;
		this.y += parent.y;
	}
	
	public void setIframe(ElementBounds iframe) {
		this.iframe = iframe;
	}
	
	public ElementBounds getIframe() {
		return iframe;
	}
}
