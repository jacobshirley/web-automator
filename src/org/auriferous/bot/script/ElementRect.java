package org.auriferous.bot.script;

import java.awt.Point;
import java.awt.Rectangle;

public class ElementRect extends Rectangle{
	public ElementRect(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public Point getRandomPointInRect() {
		return new Point((int)(x+(Math.random()*width)), (int)(y+(Math.random()*height)));
	}
}
