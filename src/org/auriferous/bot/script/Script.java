package org.auriferous.bot.script;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;

import org.auriferous.bot.Utils;
import org.auriferous.bot.input.KeyboardSimulator;
import org.auriferous.bot.input.MouseSimulator;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class Script extends LoadAdapter {
	protected ScriptContext context;
	protected Browser browser;
	
	private MouseSimulator mouse;
	private KeyboardSimulator keyboard;
	
	public static final int MOUSE_SPEED = 20;
	
	public enum ClickType {
		LCLICK, RCLICK, NOCLICK
	}

	public Script(ScriptContext context) {
		this.context = context;
		this.browser = context.getCurrentTab().getBrowserWindow();
		
		this.mouse = context.getMouse();
		this.keyboard = context.getKeyboard();
	}

	public void injectJQuery(long frameID) {
		try {
			browser.executeJavaScript(frameID,
					Utils.getText("https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void injectCode(long frameID) {
		try {
			browser.executeJavaScript(frameID, Utils.loadResource("resources/js/inject.js"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ElementRect getElementRect(long frameID, String elementCode) {
		browser.executeJavaScript(frameID,
				"var clicker_element = " + elementCode + "; var clicker_offset = clicker_element.offset();");
		
		JSValue val = browser.executeJavaScriptAndReturnValue(frameID, "clicker_element == null");
		boolean b = val.getBoolean();
		
		if (!b) {
			val = browser.executeJavaScriptAndReturnValue(frameID, "clicker_offset.left;");
			double x = val.getNumber();
	
			val = browser.executeJavaScriptAndReturnValue(frameID, "clicker_offset.top;");
			double y = val.getNumber();
	
			val = browser.executeJavaScriptAndReturnValue(frameID, "getElementWidth(clicker_element);");
			double width = val.getNumber();
	
			val = browser.executeJavaScriptAndReturnValue(frameID, "getElementHeight(clicker_element);");
			double height = val.getNumber();
	
			return new ElementRect((int) x, (int) y, (int) width, (int) height);
		} else
			return null;
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

	public ElementRect getRandomLink(long frameID) {
		return getElementRect(frameID, "getRandomLink()");
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
	
	public void mouse(int x, int y, ClickType clickType) {
		double x1 = mouse.getMouseX();
		double y1 = mouse.getMouseY();
		
		double randSpeed = ((Math.random() * MOUSE_SPEED) / 2.0 + MOUSE_SPEED) / 10.0;
		
		humanWindMouse(x1, y1, x, y, 7, 5, 10.0 / randSpeed, 15.0 / randSpeed, 10.0 * randSpeed);
		
		if (clickType == ClickType.LCLICK) {
			mouse.clickMouse(x, y, MouseEvent.BUTTON1);
		} else if (clickType == ClickType.RCLICK) {
			mouse.clickMouse(x, y, MouseEvent.BUTTON2);
		}
	}
	
	public void mouse(int x, int y) {
		mouse(x, y, ClickType.LCLICK);
	}
	
	public void moveMouse(int x, int y) {
		mouse(x, y, ClickType.NOCLICK);
	}
	
	public void mouse(Point p) {
		mouse(p.x, p.y);
	}
	
	public void moveMouse(Point p) {
		moveMouse(p.x, p.y);
	}
	
	public void scrollTo(double percentageX, double percentageY) {
		
	}
	
	public void scrollMouse(boolean up, int notches) {
		context.getMouse().scrollMouse(up, notches);
	}
	
	private double hypot(double dx, double dy) {
		return Math.sqrt((dx * dx) + (dy * dy));
	}

	private void humanWindMouse(double xs, double ys, double xe, double ye, double gravity, double wind, double minWait,
			double maxWait, double targetArea) {
		double veloX = 0, veloY = 0, windX = 0, windY = 0, veloMag, maxStep, D, randomDist, W, lastDist = 0;
		
		int MSP = MOUSE_SPEED;
		
		double sqrt2 = Math.sqrt(2);
		double sqrt3 = Math.sqrt(3);
		double sqrt5 = Math.sqrt(5);

		double dx = xe - xs;
		double dy = ye - ys;

		double distance = Math.sqrt((dx * dx) + (dy * dy));
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

			double rCnc = Math.random() * 6;
			if (rCnc == 1)
				D = Utils.randomRange(2, 3);

			if (D <= Math.round(dist))
				maxStep = D;
			else
				maxStep = Math.round(dist);

			if (dist >= targetArea) {
				windX = windX / sqrt3 + (Math.random() * (Math.round(wind) * 2 + 1) - wind) / sqrt5;
				windY = windY / sqrt3 + (Math.random() * (Math.round(wind) * 2 + 1) - wind) / sqrt5;
			} else {
				windX = windX / sqrt2;
				windY = windY / sqrt2;
			}

			veloX = veloX + windX;
			veloY = veloY + windY;
			veloX = veloX + gravity * (xe - xs) / dist;
			veloY = veloY + gravity * (ye - ys) / dist;

			if (hypot(veloX, veloY) > maxStep) {
				randomDist = maxStep / 2.0 + Math.random() * (Math.round(maxStep) / 2);
				veloMag = Math.sqrt(veloX * veloX + veloY * veloY);
				veloX = (veloX / veloMag) * randomDist;
				veloY = (veloY / veloMag) * randomDist;
			}

			double lastX = Math.round(xs);
			double lastY = Math.round(ys);
			xs = xs + veloX;
			ys = ys + veloY;

			if ((lastX != Math.round(xs)) || (lastY != Math.round(ys)))
				mouse.moveMouse((int) Math.round(xs), (int) Math.round(ys));

			W = (Math.random() * (Math.round(100 / MSP))) * 6;
			if (W < 5)
				W = 5;
			W = Math.round(W * 0.9);

			Utils.wait((int) W);
			lastDist = dist;
			if (hypot(xs - xe, ys - ye) < 1)
				break;
		}

		if ((Math.round(xe) != Math.round(xs)) || (Math.round(ye) != Math.round(ys)))
			mouse.moveMouse((int) Math.round(xe), (int) Math.round(ye));
	}
}