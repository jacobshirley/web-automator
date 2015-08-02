package org.auriferous.bot.script;

import java.awt.Point;
import java.awt.Rectangle;

import org.auriferous.bot.Utils;

public class ElementRect extends Rectangle{
	public ElementRect(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public Point getRandomPointInRect() {
		return new Point((int)(x+Utils.random(width)), (int)(y+Utils.random(height)));
	}
	
	public void appendParent(ElementRect parent) {
		this.x += parent.x;
		this.y += parent.y;
	}
}
