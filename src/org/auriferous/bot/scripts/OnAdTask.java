package org.auriferous.bot.scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ElementRect;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.tabs.TabPaintListener;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;

public class OnAdTask extends Script implements TabPaintListener{

	public OnAdTask(ScriptContext context) {
		super(context);
		
		//this.browser.loadURL("www.youtube.com");
		this.browser.loadURL("www.google.co.uk");
		this.browser.addLoadListener(this);
		//this.browser.loadURL("https://business.twitter.com/help/how-twitter-ads-work");
		//this.browser.loadURL("http://stackoverflow.com/questions/596467/how-do-i-convert-a-float-number-to-a-whole-number-in-javascript");
		//this.browser.loadURL("http://www.holidayautos.co.uk/?_$ja=cid:1510255|cgid:119904326|tsid:70217|crid:63895368&clientID=581725");
		//this.browser.loadURL("https://www.google.com/intx/en_uk/work/apps/business/products/gmail/index.html?utm_source=gdn&utm_medium=display&utm_campaign=emea-gb-en-gmail-rmkt-all-trial-120077397&utm_content=puppyscrubber");
		
		//this.browser.loadURL("https://www.youtube.com/");
		
		System.out.println(context.getScriptTab().getTitle());
	}

	private int successCode = -1;

	@Override
	public int tick() {
		return STATE_RUNNING;
	}
	
	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent event) {
		super.onDocumentLoadedInMainFrame(event);
	}
	
	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent event) {
		//System.out.println("loaded doc");
		
		if (event.isMainFrame()) {
			System.out.println("loaded...");
			
			injectJQuery(event.getFrameId());
			injectCode(event.getFrameId());

			//browser.executeJavaScript("elementFromPoint("+r.x+", "+r.getCenterY()+");");
			
			while (true) {
				ElementRect rect = getRandomLink(event.getFrameId());
				
				r = rect;
				//context.getKeyboard().type("hello");
				
				hoverElement(rect);
				//type("shirly lou");
				//type(KeyEvent.VK_ENTER);
				//scrollTo(50);
				Utils.wait(2000);
			}
			
			//context.getKeyboard().type("hello");
		}
	}
	
	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		//super.onFinishLoadingFrame(event);

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
		// TODO Auto-generated method stub
		
	}
}
