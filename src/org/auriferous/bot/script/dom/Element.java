package org.auriferous.bot.script.dom;

import com.teamdev.jxbrowser.chromium.JSObject;

public class Element {
	private ElementBounds bounds;
	private JSObject domElement;
	
	public Element(JSObject domElement) {
		this.domElement = domElement;
	}
	
	public JSObject getDOMElement() {
		return domElement;
	}
	
	public ElementBounds getBounds() {
		return bounds;
	}
}
