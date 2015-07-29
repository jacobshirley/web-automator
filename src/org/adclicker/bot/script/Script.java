package org.adclicker.bot.script;

import java.io.IOException;

import org.adclicker.bot.ScriptContext;
import org.adclicker.bot.utils.Utils;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class Script extends LoadAdapter{
	protected ScriptContext context;
	protected Browser browser;
	
	public Script(ScriptContext context) {
		this.context = context;
		this.browser = context.getBrowser();
	}
	
	public void injectJQuery(long frameID) {
		try {
			browser.executeJavaScript(frameID, Utils.getText("https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ElementRect getElementRect(long frameID, String elementCode) {
		//http://www.holidayautos.co.uk/?_$ja=cid:1510255|cgid:119904326|tsid:70217|crid:63895368&clientID=581725
		
		browser.executeJavaScript(frameID, "var clicker_element = "+elementCode+"; var clicker_offset = clicker_element.offset();");
    	
    	JSValue val = browser.executeJavaScriptAndReturnValue(frameID, "clicker_offset.left;");
		double x = val.getNumber();
		
    	val = browser.executeJavaScriptAndReturnValue(frameID, "clicker_offset.top;");
    	double y = val.getNumber();

    	val = browser.executeJavaScriptAndReturnValue(frameID, "clicker_element.width();");
		double width = val.getNumber();

    	val = browser.executeJavaScriptAndReturnValue(frameID, "clicker_element.height();");
    	double height = val.getNumber();
    	
    	return new ElementRect((int)x, (int)y, (int)width, (int)height);
	}
	
	public ElementRect findElementHighestWeight(long frameID) {
		try {
			browser.executeJavaScript(frameID, Utils.loadResource("resources/js/findLinks.js"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return getElementRect(frameID, "getRandomLink()");
	}
	
	public void clickElement(ElementRect element) {
		this.context.bot.getMouseSimulator().clickMouse((int)element.getCenterX(), (int)element.getCenterY());
	}
	
	public void hoverElement(ElementRect element) {
		this.context.bot.getMouseSimulator().moveMouse((int)element.getCenterX(), (int)element.getCenterY());
	}
}
