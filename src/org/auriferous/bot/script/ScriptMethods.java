package org.auriferous.bot.script;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.auriferous.bot.ResourceLoader;
import org.auriferous.bot.Utils;
import org.auriferous.bot.script.input.Keyboard;
import org.auriferous.bot.script.input.Mouse;
import org.auriferous.bot.tabs.Tab;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class ScriptMethods {
	protected Browser browser;
	
	private Mouse mouse;
	private Keyboard keyboard;
	
	private Tab target;
	
	public static final int DEFAULT_MOUSE_SPEED = 20;
	
	private int mouseSpeed = DEFAULT_MOUSE_SPEED;
	
	public enum ClickType {
		LCLICK, RCLICK, NO_CLICK
	}

	public ScriptMethods(Tab target) {
		this.target = target;
		
		this.browser = this.target.getBrowserWindow();
		this.mouse = new Mouse(this.target.getTabView());
		this.keyboard = new Keyboard(this.target.getTabView());
		
		this.target.addTabPaintListener(this.mouse);
		
		for (Long frame : this.browser.getFramesIds()) {
			injectJQuery(frame);
	    	injectCode(frame);
		}
		
		this.browser.addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				super.onFinishLoadingFrame(event);
				
				long frame = event.getFrameId();
				
				injectJQuery(frame);
		    	injectCode(frame);
			}
		});
	}
	
	public Tab getTarget() {
		return target;
	}

	private void injectJQuery(long frameID) {
		try {
			browser.executeJavaScript(frameID, ResourceLoader.loadResourceAsString("resources/js/jquery.min.js", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void injectCode(long frameID) {
		try {
			browser.executeJavaScript(frameID,  ResourceLoader.loadResourceAsString("resources/js/inject.js", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public ElementRect[] getElements(String... jqueryString) {
		
	}*/
	
	public ElementRect[] getElements(String jqueryString) {
		String mainHref = browser.executeJavaScriptAndReturnValue("window.location.href;").getString();
		
		for (Long frame : browser.getFramesIds()) {
			ElementRect[] elems = getElements(frame, jqueryString);

			if (elems.length > 0) {
				String href = browser.executeJavaScriptAndReturnValue(frame, "window.location.href;").getString();
				
				if (href.equals(mainHref)) {
					return elems;
				} else {
					ElementRect[] iframes = getElements("$(\"iframe[src='"+href+"']\");");
					
					if (iframes.length > 0) {
						ElementRect iframe = iframes[0];
						
						for (ElementRect rect : elems) {
							rect.x += iframe.x;
							rect.y += iframe.y;
						}
						
						return elems;
					}
				}
			}
		}

		return null;
	}
	
	public ElementRect[] getElements(long frameID, String jqueryString) {
		final List<ElementRect> rects = new ArrayList<ElementRect>();

		browser.executeJavaScript(frameID, "elems = "+ jqueryString + (jqueryString.endsWith(";") ? "" : ";"));
		
		int len = (int) browser.executeJavaScriptAndReturnValue(frameID, "elems.length;").getNumber();
		
		for (int i = 0; i < len; i++) {
			browser.executeJavaScript(frameID, "el = elems.eq("+i+"); off = el.offset2();");
			
			int x = (int) browser.executeJavaScriptAndReturnValue(frameID, "off.left;").getNumber();
			int y = (int) browser.executeJavaScriptAndReturnValue(frameID, "off.top;").getNumber();
			
			int width = (int) browser.executeJavaScriptAndReturnValue(frameID, "getElementWidth(el);").getNumber();
			int height = (int) browser.executeJavaScriptAndReturnValue(frameID, "getElementHeight(el);").getNumber();
			
			rects.add(new ElementRect(x, y, width, height));
		}

		ElementRect[] results = new ElementRect[rects.size()];
		rects.toArray(results);
		return results;
	}
	
	public double getPageXOffset() {
		return browser.executeJavaScriptAndReturnValue("window.pageXOffset").getNumber();
	}
	
	public double getPageYOffset() {
		return browser.executeJavaScriptAndReturnValue("window.pageYOffset").getNumber();
	}
	
	public double getWindowWidth() {
		return browser.executeJavaScriptAndReturnValue("$(window).width()").getNumber();
	}
	
	public double getWindowHeight() {
		return browser.executeJavaScriptAndReturnValue("$(window).height()").getNumber();
	}
	
	public ElementRect getRandomElement(String selector) {
		ElementRect[] elems = getElements(selector);

		if (elems == null)
			return null;

		return elems[(int) Math.floor(Math.random()*elems.length)];
	}
	
	public ElementRect getRandomElement(long frameID, String selector) {
		ElementRect[] elems = getElements(frameID, selector);
		return elems[(int) Math.floor(Math.random()*elems.length)];
	}
	
	public ElementRect getRandomTextField(long frameID) {
		return getRandomElement(frameID, "$(\"input[type='text']\")");
	}

	public ElementRect getRandomLink(long frameID) {
		return getRandomElement(frameID, "$(\"a, button, input[type='button'], input[type='submit']\")");
	}

	public void clickElement(ElementRect element) {
		Point p = element.getRandomPointInRect();
		
		p.x -= getPageXOffset();
		p.y -= getPageYOffset();
		
		mouse(p);
	}

	public void hoverElement(ElementRect element) {
		Point p = element.getRandomPointInRect();
		
		p.x -= getPageXOffset();
		p.y -= getPageYOffset();
		
		moveMouse(p);
	}
	
	public void setMouseSpeed(int mouseSpeed) {
		this.mouseSpeed = mouseSpeed;
	}
	
	public int getMouseSpeed() {
		return mouseSpeed;
	}
	
	public void mouse(int x, int y, ClickType clickType) {
		double x1 = mouse.getMouseX();
		double y1 = mouse.getMouseY();
		
		double randSpeed = ((Math.random() * mouseSpeed) / 2.0 + mouseSpeed) / 10.0;
		
		humanWindMouse(x1, y1, x, y, 7, 5, 10.0 / randSpeed, 15.0 / randSpeed, 10.0 * randSpeed);
		
		if (clickType == ClickType.LCLICK) {
			mouse.clickMouse(x, y, MouseEvent.BUTTON1);
		} else if (clickType == ClickType.RCLICK) {
			mouse.clickMouse(x, y, MouseEvent.BUTTON2);
		}
	}
	
	public void mouse(Point p, ClickType clickType) {
		mouse(p.x, p.y, clickType);
	}
	
	public void mouse(int x, int y) {
		mouse(x, y, ClickType.LCLICK);
	}
	
	public void moveMouse(int x, int y) {
		mouse(x, y, ClickType.NO_CLICK);
	}
	
	public void mouse(Point p) {
		mouse(p, ClickType.LCLICK);
	}
	
	public void moveMouse(Point p) {
		moveMouse(p.x, p.y);
	}
	
	public void scrollTo(double percentageX, double percentageY) {
		
	}
	
	public void scrollMouse(boolean up, int notches) {
		this.mouse.scrollMouse(up, notches);
	}
	
	private double hypot(double dx, double dy) {
		return Math.sqrt((dx * dx) + (dy * dy));
	}

	private void humanWindMouse(double xs, double ys, double xe, double ye, double gravity, double wind, double minWait,
			double maxWait, double targetArea) {
		double veloX = 0;
		double veloY = 0;
		double windX = 0;
		double windY = 0;
		double veloMag = 0;
		double maxStep = 0;
		double D = 0;
		double randomDist = 0;
		int W = 0;
		
		int MSP = mouseSpeed;
		
		double sqrt2 = Math.sqrt(2);
		double sqrt3 = Math.sqrt(3);
		double sqrt5 = Math.sqrt(5);

		double dx = xe - xs;
		double dy = ye - ys;

		double distance = hypot(dx, dy);
		long t = System.currentTimeMillis() + 10000;
		while (true) {
			if (System.currentTimeMillis() > t)
				break;

			double dist = hypot(xs - xe, ys - ye);
			wind = Math.min(wind, dist);
			if (dist < 1)
				dist = 1;

			D = (Math.round((Math.round(distance) * 0.3)) / 7);
			if (D > 25)
				D = 25;
			if (D < 5)
				D = 5;

			double rCnc = Utils.random(6);
			if (rCnc == 1)
				D = Utils.randomRange(2, 3);

			if (D <= Math.round(dist))
				maxStep = D;
			else
				maxStep = Math.round(dist);

			if (dist >= targetArea) {
				windX = windX / sqrt3 + (Utils.random(Math.round(wind) * 2 + 1) - wind) / sqrt5;
				windY = windY / sqrt3 + (Utils.random(Math.round(wind) * 2 + 1) - wind) / sqrt5;
			} else {
				windX = windX / sqrt2;
				windY = windY / sqrt2;
			}

			veloX = veloX + windX;
			veloY = veloY + windY;
			veloX = veloX + gravity * (xe - xs) / dist;
			veloY = veloY + gravity * (ye - ys) / dist;

			if (hypot(veloX, veloY) > maxStep) {
				randomDist = maxStep / 2.0 + Utils.random(Math.round(maxStep) / 2);
				veloMag = Math.sqrt((veloX * veloX) + (veloY * veloY));
				veloX = (veloX / veloMag) * randomDist;
				veloY = (veloY / veloMag) * randomDist;
			}

			int lastX = (int) Math.round(xs);
			int lastY = (int) Math.round(ys);
			xs = xs + veloX;
			ys = ys + veloY;

			if ((lastX != Math.round(xs)) || (lastY != Math.round(ys)))
				mouse.moveMouse((int) Math.round(xs), (int) Math.round(ys));

			W = (int) (Utils.random((Math.round(100/MSP)))*6);
			if (W < 5)
				W = 5;
			W = (int) Math.round(W * 0.9);

			Utils.wait((int) W);
			if (hypot(xs - xe, ys - ye) < 1)
				break;
		}

		if ((Math.round(xe) != Math.round(xs)) || (Math.round(ye) != Math.round(ys)))
			mouse.moveMouse((int) Math.round(xe), (int) Math.round(ye));
	}
	
	
	public void type(String text) {
		keyboard.type(text);
	}
	
	public void type(int id) {
		keyboard.type(id);
	}

	public Browser getBrowser() {
		return browser;
	}
}