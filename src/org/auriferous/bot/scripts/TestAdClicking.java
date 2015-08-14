package org.auriferous.bot.scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.ScriptMethods.ClickType;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabControlAdapter;
import org.auriferous.bot.tabs.view.TabPaintListener;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

public class TestAdClicking extends Script implements TabPaintListener, LoadListener{
	private ScriptMethods methods;
	private Browser browser;
	private int successCode = -1;
	private Tab currentTab;
	private int status = STATE_RUNNING;
	
	public TestAdClicking(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
		//this.browser.loadURL("naht.tk");
		//this.browser.loadURL("http://ceehu.tk/random");
		//this.browser.loadURL("https://business.twitter.com/help/how-twitter-ads-work");
		//this.browser.loadURL("http://stackoverflow.com/questions/596467/how-do-i-convert-a-float-number-to-a-whole-number-in-javascript");
		//this.browser.loadURL("http://www.holidayautos.co.uk/?_$ja=cid:1510255|cgid:119904326|tsid:70217|crid:63895368&clientID=581725");
		//this.browser.loadURL("https://www.google.com/intx/en_uk/work/apps/business/products/gmail/index.html?utm_source=gdn&utm_medium=display&utm_campaign=emea-gb-en-gmail-rmkt-all-trial-120077397&utm_content=puppyscrubber");
		
		//openTab("naht.tk/random");//
		System.out.println("Starting");
		//openTab("naht.tk/random");//
		currentTab = openTab("rxquiehm.cf/random");//openTab("https://m.audibene.com/hearing-aids-consultation-siemens/?utm_source=google&utm_medium=cpc&utm_campaign=UK_GDN_INT&gclid=CMKUuITtnscCFWoJwwodyh0KBw");//openTab("http://ceehu.tk/random");// openTab("http://trippins.tk/random");//openTab("http://ceehu.tk/random");//openTab("http://www.w3schools.com/html/tryit.asp?filename=tryhtml_input");
		
		currentTab.getTabView().addTabPaintListener(this);
		getTabs().addTabControlListener(new TabControlAdapter() {
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
	
	ElementBounds r2 = null;
	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent event) {
		
	}
	
	private ElementBounds findAds(String... jqueryStrings) {
		ElementBounds[] adsbygoogle = methods.getElements("$('.adsbygoogle')");
		
		if (adsbygoogle != null) {
			System.out.println("Found basic ad");
			
			ElementBounds bounds = adsbygoogle[0];
			ElementBounds[] iframe1 = methods.getElements("$('#google_ads_frame1')");
			if (iframe1 != null) {
				bounds.add(iframe1[0]);
				
				ElementBounds[] result = null;
				for (String s : jqueryStrings) {
					System.out.println("Trying "+s);
					result = methods.getElements(s);
					if (result != null) {
						System.out.println("Found "+s);
						bounds.add(result[0]);
						bounds.width = result[0].width;
						bounds.height = result[0].height;
						break;
					}
				}
			}
			return bounds;
		}
		
		return null;
	}
	
	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		//super.onFinishLoadingFrame(event);
		
		long frame = event.getFrameId();
		
		if (event.isMainFrame()) {
			System.out.println("Finished loading main frame");
			
			//methods.getRandomLink(false);//
			ElementBounds rects = findAds("$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");
			
			methods.moveMouse(300, 300);
			if (rects != null) {
				System.out.println("Found");
				Point p = rects.getRandomPointFromCentre(0.5, 0.5);
				
				r = rects;//iframe;
				
				//break;
				methods.mouse(p, ClickType.NO_CLICK);
			}
			/*r = methods.getElements("$('iframe');")[1];
			
			Point p = r.getRandomPointInRect();
			
			methods.mouse(p, ClickType.NO_CLICK);*/
		}
	}

	private ElementBounds r = null;
	
	@Override
	public void onPaint(Graphics g) {
		if (r != null) {
			g.setColor(Color.green);
			g.drawRect(r.x, r.y, r.width, r.height);
		}
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onTerminate() {
		
	}

	@Override
	public void onFailLoadingFrame(FailLoadingEvent event) {
	}

	@Override
	public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
	}

	@Override
	public void onStartLoadingFrame(StartLoadingEvent event) {
	}
}