package org.auriferous.webautomator.script.dom;

import java.awt.Point;
import java.awt.Rectangle;

import org.auriferous.webautomator.Utils;

import com.teamdev.jxbrowser.chromium.JSObject;

public class ElementBounds extends Rectangle{
	private ElementBounds iframe;
	private JSObject domElement;
	
	public ElementBounds(JSObject domElement, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.domElement = domElement;
	}
	
	public Point getRandomPoint() {
		return new Point(x+Utils.random(width), y+Utils.random(height));
	}
	
	public Point getRandomPointFromCentre(double percX, double percY) {
		double w = width/2;
		double h = height/2;

		int xx = (int) (getCenterX()+(w*Utils.random(-percX, percX)));
		int yy = (int) (getCenterY()+(h*Utils.random(-percY, percY)));
		
		return new Point(xx, yy);
	}
	
	public void add(ElementBounds parent) {
		this.x += parent.x;
		this.y += parent.y;
	}
	
	public void setIframe(ElementBounds iframe) {
		this.iframe = iframe;
	}
	
	public ElementBounds getIframe() {
		return iframe;
	}
	
	public void setDOMElement(JSObject domElement) {
		this.domElement = domElement;
	}
	
	public JSObject getDOMElement() {
		return domElement;
	}
}
