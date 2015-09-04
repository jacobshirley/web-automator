package org.auriferous.bot.script;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.auriferous.bot.ResourceLoader;
import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods.ClickType;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.input.Keyboard;
import org.auriferous.bot.script.input.Mouse;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabCallback;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.StatusEvent;
import com.teamdev.jxbrowser.chromium.events.StatusListener;

public class ScriptMethods {
	protected Browser browser;
	
	private Mouse mouse;
	private Keyboard keyboard;
	
	private Tab target;
	
	public static final int DEFAULT_MOUSE_SPEED = 25;
	
	private int mouseSpeed = DEFAULT_MOUSE_SPEED;
	
	public static final String SHIFT_KEYS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ¬!\"£$%^&*()_+{}:@~<>?|€";
	public static final int DEFAULT_KEY_TIME = 100;
	
	public enum ClickType {
		LCLICK, RCLICK, NO_CLICK
	}
	
	private String status = "";

	public ScriptMethods(Tab target) {
		this.target = target;
		
		this.browser = this.target.getBrowserWindow();
		
		this.mouse = new Mouse(this.target.getTabView());
		this.keyboard = new Keyboard(this.target.getTabView());
		
		this.target.getTabView().addTabPaintListener(this.mouse);
		
		this.browser.addStatusListener(new StatusListener() {
			@Override
			public void onStatusChange(StatusEvent event) {
				status = event.getText();
			}
		});
	}
	
	public Tab getTarget() {
		return target;
	}

	private void injectJQuery(long frameID) {
		try {
			if (!isJQueryInjected(frameID)) {
				browser.executeJavaScript(frameID, ResourceLoader.loadResourceAsString("resources/js/jquery.min.js", true));
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private void injectCode(long frameID) {
		try {
			if (!isCodeInjected(frameID)) {
				browser.executeJavaScript(frameID,  ResourceLoader.loadResourceAsString("resources/js/inject.js", true));
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private boolean isJQueryInjected(long frameID) {
		return browser.executeJavaScriptAndReturnValue(frameID, "window.jQuery != undefined").getBoolean();
	}
	
	private boolean isCodeInjected(long frameID) {
		return browser.executeJavaScriptAndReturnValue(frameID, "window.injectionLoaded != undefined").getBoolean();
	}
	
	/*public ElementRect[] getElements(String... jqueryString) {
		
	}*/
	
	public String getStatus() {
		return status;
	}
	
	public ElementBounds[] getElements(String jqueryString) {
		ElementBounds testEl = null;
		ElementBounds[] foundEls = null;
		
		for (Long frame : browser.getFramesIds()) {
			ElementBounds[] elems = getElements(frame, jqueryString);
			if (elems.length > 0) {
				if (testEl == null) {
					foundEls = elems;
					testEl = elems[0];
				} else {
					if (elems[0].x > testEl.x && elems[0].y > testEl.y) {
						foundEls = elems;
						testEl = elems[0];
					}
				}
			}
		}
		
		return foundEls;
	}
	
	public ElementBounds[] getElementsInIFrames(String jqueryString) {
		String mainHref = browser.executeJavaScriptAndReturnValue("window.location.href;").getString();
		String href = "";
		
		ElementBounds testEl = null;
		ElementBounds[] foundEls = null;
		
		for (Long frame : browser.getFramesIds()) {
			ElementBounds[] elems = getElements(frame, jqueryString);
			if (elems.length > 0) {
				//System.out.println("Testing "+jqueryString);
				//System.out.println(browser.getHTML(frame));
				if (testEl == null) {
					href = browser.executeJavaScriptAndReturnValue(frame, "window.location.href;").getString();

					foundEls = elems;
					testEl = elems[0];
				} else {
					if (elems[0].x > testEl.x && elems[0].y > testEl.y) {
						//System.out.println("Getting highest offset");
						href = browser.executeJavaScriptAndReturnValue(frame, "window.location.href;").getString();
						
						foundEls = elems;
						testEl = elems[0];
					}
				}
			}
		}
		
		if (foundEls != null) {
			if (href.equals(mainHref)) {
				return foundEls;
			} else {
				//System.out.println("Searching iframes");
				for (Long frame : browser.getFramesIds()) {
					ElementBounds[] iframes = getElements(frame, "$(\"iframe[src='"+href+"']\");");
					
					if (iframes.length > 0) {
						ElementBounds iframe = iframes[0];
						
						//System.out.println("Found iframe "+iframes.length + " with src "+href);
						
						for (ElementBounds rect : foundEls) {
							rect.setIframe(iframe);
							rect.x += iframe.x;
							rect.y += iframe.y;
						}
						
						return foundEls;
					} else {
						//System.out.println("not found");
					}
				}
			}
		}

		return null;
	}
	
	public ElementBounds[] getElements(long frameID, String jqueryString) {
		final List<ElementBounds> rects = new ArrayList<ElementBounds>();
		injectJQuery(frameID);
    	injectCode(frameID);

    	try {
    		if (jqueryString.endsWith(";"))
    			jqueryString = jqueryString.substring(0, jqueryString.length()-1);
    			
    		String search = jqueryString;

    		TabCallback c = new TabCallback() {
    			@Override
    			public Object onInvoke(Object... args) {
    				double x = Double.parseDouble(args[1].toString());
    				double y = Double.parseDouble(args[2].toString());
    				double width = Double.parseDouble(args[3].toString());
    				double height = Double.parseDouble(args[4].toString());
    				
    				rects.add(new ElementBounds((int)x, (int)y, (int)width, (int)height));

    				return null;
    			}
    		};
    		
	    	target.pushCallback(c);

			browser.executeJavaScriptAndReturnValue(frameID, "sendBackResults("+search+")");
			
			target.popCallback();
    	} catch (Exception e) {
    		//e.printStackTrace();
    	}
    	
		ElementBounds[] results = new ElementBounds[rects.size()];
		rects.toArray(results);
		return results;
	}
	
	public double getPageXOffset() {
		return browser.executeJavaScriptAndReturnValue("window.pageXOffset").getNumber();
	}
	
	public double getPageYOffset() {
		return browser.executeJavaScriptAndReturnValue("window.pageYOffset").getNumber();
	}
	
	public double getPageWidth() {
		return browser.executeJavaScriptAndReturnValue("$(document).width()").getNumber();
	}
	
	public double getPageHeight() {
		return browser.executeJavaScriptAndReturnValue("$(document).height()").getNumber();
	}
	
	public double getWindowWidth() {
		return browser.executeJavaScriptAndReturnValue("$(window).width()").getNumber();
	}
	
	public double getWindowHeight() {
		return browser.executeJavaScriptAndReturnValue("$(window).height()").getNumber();
	}
	
	public ElementBounds getRandomElement(String... selector) {
		List<ElementBounds> elemsList = new ArrayList<ElementBounds>();
		for (String s : selector) {
			
			ElementBounds[] elems = getElementsInIFrames(s);
	
			if (elems == null)
				continue;
			
			elemsList.addAll(Arrays.asList(elems));
		}
		
		if (elemsList.isEmpty())
			return null;
	
		return elemsList.get((int) Math.floor(Math.random()*elemsList.size()));
	}
	
	public ElementBounds getRandomElement(long frameID, String... selector) {
		List<ElementBounds> elemsList = new ArrayList<ElementBounds>();
		
		for (String s : selector) {
			ElementBounds[] elems = getElements(frameID, s);
	
			if (elems == null)
				continue;
			
			elemsList.addAll(Arrays.asList(elems));
		}
		
		if (elemsList.isEmpty())
			return null;
	
		return elemsList.get((int) Math.floor(Math.random()*elemsList.size()));
	}
	
	public ElementBounds getRandomTextField(long frameID) {
		return getRandomElement(frameID, "$(\"input[type='text']\")");
	}
	
	public ElementBounds getRandomClickable(boolean includeButtons) {
		ElementBounds el = null;
		if (includeButtons)
			el = getRandomElement("$(document).findVisibles(\"a, button, input[type='button'], input[type='submit']\");", "getJSClickables();");
		else {
			el = getRandomElement("$(document).findVisibles('a[href^=\"http\"], a[href^=\"/\"]');", "getJSClickables();");
		}
		return el;
	}

	public ElementBounds getRandomClickable(long frameID, boolean includeButtons) {
		ElementBounds el = null;
		if (includeButtons)
			el = getRandomElement(frameID, "$(document).findVisibles(\"a, button, input[type='button'], input[type='submit']\");", "getJSClickables();");
		else {
			el = getRandomElement(frameID, "$(document).findVisibles('a[href^=\"http\"], a[href^=\"/\"]');", "getJSClickables();");
		}
		return el;
	}

	public void clickElement(ElementBounds element) {
		Point p = element.getRandomPoint();
		
		p.x -= getPageXOffset();
		p.y -= getPageYOffset();
		
		mouse(p);
	}

	public void hoverElement(ElementBounds element) {
		Point p = element.getRandomPoint();
		
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
	
	public void scrollTo(int y, int timeWait, int randomTime) {
		while (y - getPageYOffset() >= target.getTabView().getHeight()) {
			Utils.wait(timeWait+Utils.random(randomTime));
			scrollMouse(false, 3);
		}
		
		while (y - getPageYOffset() <= 0) {
			Utils.wait(timeWait+Utils.random(randomTime));
			scrollMouse(true, 3);
		}
	}
	
	public void scrollTo(int y) {
		scrollTo(y, 10, 20);
	}
	
	public void moveMouseRandom() {
		int randomY = (int)Math.round(Math.random()*getPageHeight());
		System.out.println("moving mouse to "+randomY);
		mouse((int)Math.round(Math.random()*getWindowWidth()), randomY, ClickType.NO_CLICK);
	}
	
	public void mouse(int x, int y, ClickType clickType) {
		scrollTo(y);
		
		x -= getPageXOffset();
		y -= getPageYOffset();
		
		if (x <= target.getTabView().getWidth()) {
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
	
	public void scrollMouse(boolean up, int notches) {
		this.mouse.scrollMouse(up, notches);
		Utils.wait(50+Utils.random(20));
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
				D = Utils.random(2, 3);

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

			W = Utils.random((Math.round(100/MSP)))*6;
			if (W < 5)
				W = 5;
			W = (int) Math.round(W * 0.9);

			Utils.wait(W);
			if (hypot(xs - xe, ys - ye) < 1)
				break;
		}

		if ((Math.round(xe) != Math.round(xs)) || (Math.round(ye) != Math.round(ys)))
			mouse.moveMouse((int) Math.round(xe), (int) Math.round(ye));
	}
	
	public final void type(String message) {
		boolean shiftDown = false;
		int mods = 0;
		for (char c : message.toCharArray()) {
			boolean isShiftKey = SHIFT_KEYS.contains(""+(char)c);
			if (!shiftDown) {
				if (isShiftKey) {
					shiftDown = true;
					keyboard.pressKey(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK);
					mods |= InputEvent.SHIFT_DOWN_MASK;
				} else
					mods = 0;
			} else if (shiftDown && !isShiftKey) {
				shiftDown = false;
				keyboard.releaseKey(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK);
				mods = 0;
			}
			keyboard.typeKey(c, DEFAULT_KEY_TIME, mods);
		}
	}
	
	public void type(int c, int time) {
		keyboard.typeKey(c, time, 0);
	}
	
	public final void type(int c) {
		int mods = 0;
		if (SHIFT_KEYS.contains(""+(char)c)) {
			keyboard.typeKey(KeyEvent.VK_SHIFT, DEFAULT_KEY_TIME, InputEvent.SHIFT_DOWN_MASK);
			mods |= InputEvent.SHIFT_DOWN_MASK;
		}
		keyboard.typeKey(c, DEFAULT_KEY_TIME, mods);
	}

	public Browser getBrowser() {
		return browser;
	}
}