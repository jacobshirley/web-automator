package org.auriferous.bot.scripts.adclicker.states;

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
import org.auriferous.bot.data.DataEntry;
import org.auriferous.bot.data.history.HistoryEntry;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.states.events.Events;
import org.auriferous.bot.tabs.Tab;

import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;

public class ClickAdState extends AdClickerState {
	private static final String ADS_BY_GOOGLE = "$('.adsbygoogle').css('position', 'fixed').css('display', 'block').css('z-index', '99999999').css('left', '0px').css('top', '0px').show()";
	private static final String ASWIFT = "$('ins[id^=\"aswift_\"][id$=_anchor]').css('position', 'fixed').css('display', 'block').css('z-index', '99999998').css('left', '0px').css('top', '0px').show()";
	
	private static final String[] AD_ELEMENT_SEARCHES = new String[] {ASWIFT};
	
	private static final int MAX_CLICKS = 4;
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

	private String currentTaskURL;
	private String blogURL = null;
	
	private int searchAdTries = 0;
	private boolean clickedAd = false;
	private boolean pageLoading = false;

	public ClickAdState(AdClicker adClicker) {
		super(adClicker);
		
		final Browser browser = adClicker.getBotTab().getBrowserInstance();
		browser.getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate() {
			@Override
			public void onBeforeURLRequest(BeforeURLRequestParams arg0) {
				pageLoading = true;
				String url = arg0.getURL();
				//System.out.println("Getting url "+url);
				if (url.contains("aclk?")) {
					handleAdTest(url);
				}
			}
		});
	}
	
	private void removeAllElementsButOne(Browser browser, ScriptMethods methods, String jquery) {
		for (long id : browser.getFramesIds()) {
			methods.injectJQuery(id);
			methods.injectCode(id);
			try {
				browser.executeJavaScriptAndReturnValue(id, "removeAllButOne("+jquery+");");
			} catch (Exception e) {}
		}
	}
	
	private void removeAllElements(Browser browser, ScriptMethods methods, String jquery) {
		for (long id : browser.getFramesIds()) {
			methods.injectJQuery(id);
			methods.injectCode(id);
			try {
				browser.executeJavaScriptAndReturnValue(id, "removeAll("+jquery+");");
			} catch (Exception e) {}
		}
	}
	
	@Override
	public State process(List<Integer> events) {
		System.out.println("Started ad clicking");
    	Utils.wait(2000);
    	
    	ScriptMethods methods = adClicker.getScriptMethods();
    	Tab botTab = adClicker.getBotTab();
    	botTab.setBlockJSMessages(false);
    	String url = botTab.getURL();
    	
    	if (blogURL != null && !url.contains(Utils.getBaseURL(currentTaskURL))) {
    		System.out.println("Clicked ad successfully.");
    		
    		return new CheckAdState(adClicker, this);
    	} else {
    		currentTaskURL = url;
    		
        	ElementBounds adElement = findAds(botTab, "$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");
        	pageLoading = false;
        	if (adElement != null) {
        		blogURL = botTab.getURL();
        		
        		adElement.width -= 35;
        		
        		adClicker.setDebugElement(adElement);
        		
        		for (int i = 0; i < 10; i++) {
        			//if (!botTab.getURL().contains(Utils.getBaseURL(currentTaskURL)))
        			//	return new CheckAdState(adClicker, this);
        			
        			clickedAd = true;
	        		Point p = adElement.getRandomPointFromCentre(0.5, 0.5);

            		methods.mouse(p.x, p.y);
            		
            		Utils.wait(1000);
            		
            		if (pageLoading) {
            			System.out.println("Page loading!!!");
            			return new CheckAdState(adClicker, this);
            		}
        		}
        	}
        	if (searchAdTries < 10) {
        		searchAdTries++;
        		System.out.println("Couldn't find ad on try "+searchAdTries+"/10. Reloading page.");
        		
        		botTab.reload();
        		
        		return this;
        	} else if (searchAdTries == 10){
        		System.out.println("Couldn't find ad. Next task...");
        		
        		return new TaskNextState(adClicker);
        	}
    	}
    	
    	return this;
	}
	
	private ElementBounds findAds(Tab botTab, String... jqueryStrings) {
		ScriptMethods methods = adClicker.getScriptMethods();

		List<String> randomList = Arrays.asList(AD_ELEMENT_SEARCHES);
		Collections.shuffle(randomList);
		
		for (String search : randomList) {
			ElementBounds[] rootAds = methods.getElements(search);
			
			if (rootAds.length > 0) {
				removeAllElementsButOne(botTab.getBrowserInstance(), methods, search);
			
				/*for (String search2 : randomList) {
					if (!search2.equals(search)) {
						System.out.println("Removing "+search2);
						removeAllElements(botTab.getBrowserInstance(), methods, search2);
					}
				}*/
				ElementBounds bounds = rootAds[0];
				ElementBounds[] iframe1 = methods.getElements("$('#google_ads_frame1')");
				
				if (iframe1.length > 0) {
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
			System.out.println("url req: "+url);
			DataEntry historyConfig = adClicker.getHistoryConfig();
		
			clickedAd = false;
			try {
				System.out.println("Testing url");
				url = testAdURL(url).replace("https://", "http://");
				int id = url.lastIndexOf("?");
				if (id > 0)
					url = url.substring(0, id);
				
				url = Utils.getBaseURL(url);
				
				System.out.println("Getting url of "+url);

				if (!historyConfig.contains("//history-entry[url/@value='"+url+"']")) {
					DataEntry entry = new HistoryEntry("", "", url);
					
					if (historyConfig.size() > 3) {
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
    
    private String testAdURL(String testURL) throws Exception {
		String url = "http://www.wtfhallo.co/test_url.php";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "url="+URLEncoder.encode(testURL, "UTF-8");

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + urlParameters);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return URLDecoder.decode(response.toString(), "UTF-8");
    }

	public String getCurrentTaskURL() {
		return currentTaskURL;
	}
	
	public void incSearchAdTries() {
		searchAdTries++;
	}
}
