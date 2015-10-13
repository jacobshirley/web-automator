package org.auriferous.bot.scripts.blogscripts.states.adclicker;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.callbacks.JSCallback;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.scripts.blogscripts.AdClicker;
import org.auriferous.bot.scripts.blogscripts.events.Events;
import org.auriferous.bot.scripts.blogscripts.states.TaskErrorState;
import org.auriferous.bot.scripts.blogscripts.task.Task;
import org.auriferous.bot.shared.data.DataEntry;
import org.auriferous.bot.shared.data.history.HistoryEntry;
import org.auriferous.bot.shared.fsm.State;
import org.auriferous.bot.shared.tabs.Tab;

import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;

public class ClickAdState extends AdClickerState {
	private static final String ADS_BY_GOOGLE = "$('.adsbygoogle').css('position', 'fixed').css('display', 'block').css('z-index', '99999999').css('left', '0px').css('top', '0px').show()";
	private static final String ASWIFT = "$('ins[id^=\"aswift_\"][id$=\"_anchor\"]').css('position', 'fixed').css('display', 'block').css('z-index', '99999999').css('left', '0px').css('top', '0px').show()";
	
	private static final String MOVE_ALL_ELEMENTS = "$('body').children('*').css('position', 'absolute').css('left', '100%').css('top', '100%')";
	
	private static final String[] AD_ELEMENT_SEARCHES = new String[] {ASWIFT};
	
	private static final int MAX_CLICKS = 5;
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
	private static final int MAX_SEARCH_TRIES = 10;

	private String currentTaskURL;
	
	private int searchAdTries = 0;
	private boolean clickedAd = false;
	private boolean pageLoading = false;
	
	private boolean triggerError = false;
	
	private boolean reloadingPage = false;
	
	private ScriptMethods methods = null;
	
	private boolean shouldCheckAdOnClick = false;

	public ClickAdState(AdClicker adClicker, String curURL) {
		super(adClicker);
		
		this.currentTaskURL = curURL;
		
		final Browser browser = adClicker.getBotTab().getBrowserInstance();
		browser.getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate() {
			@Override
			public void onBeforeURLRequest(BeforeURLRequestParams event) {
				String url = event.getURL();
				
				//System.out.println("Getting url "+url);
				
				if (url.contains("&adurl=")) {
					pageLoading = true;
					if (shouldCheckAdOnClick)
						handleAdTest(url);
				}
			}
		});
	}
	
	@Override
	public State process(List<Integer> events) {
		if (this.reloadingPage && !events.contains(Events.EVENT_PAGE_LOADED))
			return this;
		
		if (searchAdTries >= MAX_SEARCH_TRIES){
    		System.out.println("Couldn't find ad. Next task...");
    		
    		return new TaskErrorState(adClicker, Task.STATUS_FAILED, "Couldn't find ad.");
    	}
		
		if (this.triggerError) {
			this.triggerError = false;
    		
    		System.out.println("Couldn't find ad on try "+searchAdTries+"/10.");
    	}

    	methods = adClicker.getScriptMethods();
    	Tab botTab = adClicker.getBotTab();
 
		if (!adClicker.onBlog()) {
			adClicker.resetTimer();
			System.out.println("Not in blog. Going back.");
			this.reloadingPage = true;
			adClicker.loadBlog();
			return this;
		}

		reloadingPage = false;
		searchAdTries++;
		
		System.out.println("Started ad clicking");
		Utils.wait(3000);

		//methods.execJS(MOVE_ALL_ELEMENTS);
		
    	ElementBounds adElement = findAds(botTab, "$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");
    	pageLoading = false;
    	//moveElements("$('ins[id^=\"aswift_\"][id$=\"_anchor\"]')", "$('body')");
    	
    	//methods.execJS("$('body').find('div').not('ins[id^=\"aswift_\"][id$=\"_anchor\"]').css('position', 'absolute').css('right', '100%')");

    	
    	if (adElement != null) {
    		adElement.width -= 35;
    		
    		adClicker.setDebugElement(adElement);
    		
    		String adURL = getBaseAdURL();
    		DataEntry historyConfig = adClicker.getHistoryConfig();
    		
    		shouldCheckAdOnClick = true;
    		
    		if (!adURL.equals("")) {
    			if (!doesBlackListContain(adURL) && !historyConfig.contains("//history-entry[url/@value='"+adURL+"']")) {
    				System.out.println("Haven't clicked "+adURL +" recently!");
    				DataEntry entry = new HistoryEntry("", "", adURL);
    				
    				if (historyConfig.size() >= MAX_CLICKS) {
    					historyConfig.remove(0);
    					historyConfig.add(entry);
    				} else
    					historyConfig.add(entry);
    				
    				shouldCheckAdOnClick = false;
    			} else {
    				System.out.println("Already clicked this recently, or it was blacklist.");
    				
    				triggerError();
    				this.reloadingPage = true;
    				botTab.reload();
    				return this;
    			}
    		} else {
    			System.out.println("Couldn't find adurl in any element");
    			shouldCheckAdOnClick = true;
    		}
    		
    		for (int i = 0; i < 10; i++) {
    			clickedAd = true;
        		Point p = adElement.getRandomPointFromCentre(0.5, 0.5);

        		methods.mouse(p.x, p.y);
        		
        		Utils.wait(1000);
        		
        		if (pageLoading) {
        			System.out.println("Page loading!!!");
        			break;
        		}
    		}
    	} else {
    		System.out.println("Couldn't find ad.");
    		this.triggerError = true;
    		//this.reloadingPage = true;
    		botTab.reload();
    	}
    	
    	adClicker.resetTimer();
    	return new CheckAndWaitAdState(adClicker, this);
	}
	
	private synchronized String getBaseAdURL() {
		class CheckerCallback implements JSCallback {
			private String url = "";

			public boolean onResult(JSValue value) {
				if (!value.isNull()) {
					url = value.getString();
					return true;
				}
				return false;
			}
		}
		CheckerCallback callback = new CheckerCallback();
		methods.execJS("getAdURL();", callback);
		
		String url = callback.url;
		if (!url.equals("")) {
			url = url.split("&adurl=")[1];
		}
		if (url.equals(""))
			return "";
		
		System.out.println(url);
		
		return Utils.getBaseURL(url);
	}
	
	public void triggerError() {
		this.triggerError = true;
	}
	
	private void removeAllElementsButOne(String jquery) {
		methods.execJS("removeAllButOne("+jquery+");");
	}
	
	private void removeAllElements(String jquery) {
		methods.execJS("removeAll("+jquery+");");
	}
	
	private void moveElements(String jquery, String jqueryParent) {
		methods.execJS("move("+jquery+", "+jqueryParent+");");	
	}
	
	private ElementBounds findAds(Tab botTab, String... jqueryStrings) {
		ScriptMethods methods = adClicker.getScriptMethods();

		List<String> randomList = Arrays.asList(AD_ELEMENT_SEARCHES);
		Collections.shuffle(randomList);
		
		for (String search : randomList) {
			ElementBounds[] rootAds = methods.getElements(search);
			
			if (rootAds.length > 0) {
				//moveElements(botTab.getBrowserInstance(), methods, search, "$('body')");
				//removeAllElementsButOne(search);
			
				/*for (String search2 : randomList) {
					if (!search2.equals(search)) {
						System.out.println("Removing "+search2);
						removeAllElements(botTab.getBrowserInstance(), methods, search2);
					}
				}*/
				ElementBounds bounds = rootAds[0];
				ElementBounds[] iframe1 = methods.getElements(adClicker.getMainFrameID(), "$('iframe[id^=\"google_ads_frame\"]')");
				
				if (iframe1.length > 0) {
					System.out.println("Found google_ads_frame");
					bounds.add(iframe1[0]);
					
					ElementBounds[] result = null;
					for (String s : jqueryStrings) {
						result = methods.getElements(s);
						if (result.length > 0) {
							System.out.println("Found "+s);
							
							bounds.add(result[0]);
							bounds.width = result[0].width;
							bounds.height = result[0].height;
							
							bounds.setDOMElement(result[0].getDOMElement());

							break;
						}
					}
				}
				return bounds;
			}
		}
		
		return null;
	}
	
	private void handleAdTest(String url) {
		if (clickedAd) {
			DataEntry historyConfig = adClicker.getHistoryConfig();
		
			clickedAd = false;
			try {
				if (!url.equals("")) {
					url = url.split("&adurl=")[1];
				}
				
				System.out.println("Getting url of "+url);

				if (!doesBlackListContain(url) && !historyConfig.contains("//history-entry[url/@value='"+url+"']")) {
					DataEntry entry = new HistoryEntry("", "", url);
					
					if (historyConfig.size() >= MAX_CLICKS) {
						historyConfig.remove(0);
						historyConfig.add(entry);
					} else
						historyConfig.add(entry);
				} else {
					System.out.println("Already clicked this.");
					adClicker.getBotTab().stop();

					new Thread(new Runnable() {
						@Override
						public void run() {
							Utils.wait(2000);
							adClicker.loadBlog();
						}
					}).start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private boolean doesBlackListContain(String url) {
		List<String> blacklist = adClicker.getBlacklist();
		
		for (String s : blacklist) {
			if (!s.equals("") && url.matches(s)) {
				return true;
			}
		}
		return false;
	}
}