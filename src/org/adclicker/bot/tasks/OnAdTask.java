package org.adclicker.bot.tasks;

import java.awt.Color;
import java.awt.Graphics;

import org.adclicker.bot.ScriptContext;
import org.adclicker.bot.script.ElementRect;
import org.adclicker.bot.script.Script;
import org.adclicker.bot.tabs.TabControlListener;
import org.adclicker.bot.tabs.TabPaintListener;
import org.adclicker.bot.utils.Utils;

import com.teamdev.jxbrowser.chromium.dom.DOMNodeAtPoint;
import com.teamdev.jxbrowser.chromium.dom.internal.NodeAtPoint;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

public class OnAdTask extends Script implements BotTask, TabPaintListener{

	public OnAdTask(ScriptContext context) {
		super(context);
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPercentageComplete() {
		// TODO Auto-generated method stub
		return 0;
	}
	private int successCode = -1;

	@Override
	public int perform() {
		this.browser.addLoadListener(this);
		//this.browser.loadURL("https://business.twitter.com/help/how-twitter-ads-work");
		//this.browser.loadURL("http://stackoverflow.com/questions/596467/how-do-i-convert-a-float-number-to-a-whole-number-in-javascript");
		//this.browser.loadURL("http://www.holidayautos.co.uk/?_$ja=cid:1510255|cgid:119904326|tsid:70217|crid:63895368&clientID=581725");
		this.browser.loadURL("https://www.youtube.com/");
		//this.browser.loadURL("https://www.google.com/intx/en_uk/work/apps/business/products/gmail/index.html?utm_source=gdn&utm_medium=display&utm_campaign=emea-gb-en-gmail-rmkt-all-trial-120077397&utm_content=puppyscrubber");
		while (successCode == -1) {
			Thread.yield();
		}
		
		return 0;
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
				//ElementRect rect = getRandomLink(event.getFrameId());
				
				//hoverElement(rect);
				//scrollTo(50);
				Utils.wait(2000);
			}
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
}
