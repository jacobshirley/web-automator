package org.auriferous.bot.scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ElementRect;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabControlAdapter;
import org.auriferous.bot.tabs.TabControlListener;
import org.auriferous.bot.tabs.TabListener;
import org.auriferous.bot.tabs.TabPaintListener;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

public class OnAdTask extends Script implements TabPaintListener, LoadListener{
	private ScriptMethods methods;
	private Browser browser;
	private int successCode = -1;
	private Tab currentTab;
	private int status = STATE_RUNNING;
	
	public OnAdTask(ScriptContext context) {
		super(context);
		//this.browser.loadURL("naht.tk");
		//this.browser.loadURL("http://ceehu.tk/random");
		//this.browser.loadURL("https://business.twitter.com/help/how-twitter-ads-work");
		//this.browser.loadURL("http://stackoverflow.com/questions/596467/how-do-i-convert-a-float-number-to-a-whole-number-in-javascript");
		//this.browser.loadURL("http://www.holidayautos.co.uk/?_$ja=cid:1510255|cgid:119904326|tsid:70217|crid:63895368&clientID=581725");
		//this.browser.loadURL("https://www.google.com/intx/en_uk/work/apps/business/products/gmail/index.html?utm_source=gdn&utm_medium=display&utm_campaign=emea-gb-en-gmail-rmkt-all-trial-120077397&utm_content=puppyscrubber");
		
		currentTab = openTab("http://www.w3schools.com/html/tryit.asp?filename=tryhtml_input");
		context.getTabs().addTabControlListener(new TabControlAdapter() {
			@Override
			public void onTabClosed(Tab tab) {
				super.onTabClosed(tab);
				
				if (tab.equals(currentTab)) {
					status = STATE_EXIT_SUCCESS;
				}
			}
		});
		
		methods = new ScriptMethods(currentTab);
		browser = this.methods.getBrowser();
		browser.addLoadListener(this);
	}

	@Override
	public int tick() {
		return status;
	}
	
	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent event) {
	}
	
	ElementRect r2 = null;
	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent event) {
		
	}
	
	/*private void findElement(String sel) {
		long elFrame = -1;
		String href = "";
		ElementRect el = null;
		
		HashMap<Long, String> frameMap = new HashMap<Long, String>();
		for (Long frame : browser.getFramesIds()) {
			injectJQuery(frame);
			injectCode(frame);
			
			String s = browser.executeJavaScriptAndReturnValue(frame, "window.location.href").getString();
			frameMap.put(frame, s);
			if (el == null) {
				el = getElementRect(frame, sel);
				if (el != null) {
					href = s;
					elFrame = frame;
				}
			}
		}
		
		for (Long frame : browser.getFramesIds()) {
			ElementRect iframe = getElementRect(frame, "$('iframe[src=\""+href+"\"')");
			
			if (iframe != null) {
				
			}
		}
	}*/
	
	public Point getElementOffset(long frame, String sel) {
		browser.executeJavaScriptAndReturnValue(frame, "var off = getElementOffIframes(\"input[type='text']\");");
		
		double x = browser.executeJavaScriptAndReturnValue(frame, "off.x").getNumber();
		double y = browser.executeJavaScriptAndReturnValue(frame, "off.y").getNumber();
		
		return new Point((int)x, (int)y);
	}
	
	
	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		//super.onFinishLoadingFrame(event);
		
		if (event.isMainFrame()) {
			methods.injectJQuery(event.getFrameId());
			methods.injectCode(event.getFrameId());
			
			//Point p = getElementOffset(event.getFrameId(), "input[type='text']");
			methods.mouse(300, 300);
			
			/*r2 = getElementRect(event.getFrameId(), "$('#iframeResult').contents().find('input').first()");//getElementRect(event.getFrameId(), "$('.adsbygoogle')");
			System.out.println("r2 "+r2);
			for (Long frame : browser.getFramesIds()) {
				if (frame != event.getFrameId()) {
					injectJQuery(frame);
					injectCode(frame);
					
					JSValue v = browser.executeJavaScriptAndReturnValue(frame, "window.location.href");
					String href = v.getString();
					
					ElementRect iframe = getElementRect(event.getFrameId(), "$('iframe[src=\""+href+"\"')");
					System.out.println(iframe);
					
					if (iframe.x > 0)
						r = iframe;
					
					/*ElementRect rect = getElementRect(frame, "$('#google_ads_frame1, #ad_iframe')");//getElementRect(frame, "$('a[href^=\"http://www.googleadservices.com/\"]')");
					//if (rect == null)
						//rect = getElementRect(frame, "$('a[href^=\"http://googleads.g.doubleclick.net/\"]')");
					
					System.out.println("found "+rect);
					if (rect != null) {
						if (rect.width > 0 ) {
							rect.appendParent(r2);

							r = rect;
							
							hoverElement(rect);
							//type("shirly lou");
							//type(KeyEvent.VK_ENTER);
							//scrollTo(50);
							Utils.wait(500);
						}
					}
				}
			}*/
		}
	}

	private ElementRect r = null;
	
	@Override
	public void onPaint(Graphics g) {
		if (r != null) {
			g.setColor(Color.green);
			g.drawRect(r.x, r.y, r.width, r.height);
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTerminate() {
		
	}

	@Override
	public void onFailLoadingFrame(FailLoadingEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartLoadingFrame(StartLoadingEvent event) {
		// TODO Auto-generated method stub
		
	}
}
