package org.auriferous.bot.scripts.adclicker.states;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.data.DataEntry;
import org.auriferous.bot.data.history.HistoryEntry;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.states.events.Events;
import org.auriferous.bot.tabs.Tab;

import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;

public class ClickAdState extends AdClickerState {
	private static final int MAX_CLICKS = 4;
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

	private String currentTaskURL;
	private String blogURL = null;
	
	private int searchAdTries = 0;
	private int foundID = 0;
	private boolean clickedAd = false;

	public ClickAdState(AdClicker adClicker) {
		super(adClicker);
		
		final Browser browser = adClicker.getBotTab().getBrowserInstance();
		browser.getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate() {
			@Override
			public void onBeforeURLRequest(BeforeURLRequestParams arg0) {
				handleAdTest(arg0.getURL());
			}
		});
	}
	
	@Override
	public State process(List<Integer> events) {
		System.out.println("Started ad clicking");
    	Utils.wait(2000);
    	
    	ScriptMethods methods = adClicker.getScriptMethods();
    	Tab botTab = adClicker.getBotTab();
    	String url = botTab.getURL();
    	
    	if (blogURL != null && !url.contains(Utils.getBaseURL(currentTaskURL))) {
    		System.out.println("Clicked ad successfully.");
    		
    		return new WaitOnAdState(adClicker);
    	} else {
    		currentTaskURL = url;
    		
        	ElementBounds adElement = findAds("$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");

        	if (adElement != null) {
        		blogURL = botTab.getURL();
        		
        		adElement.width -= 35;
        		
        		adClicker.setDebugElement(adElement);
        		
        		for (int i = 0; i < 10; i++) {
        			if (!botTab.getURL().contains(Utils.getBaseURL(currentTaskURL)))
        				return new WaitOnAdState(adClicker);
        			
	        		Point p = adElement.getRandomPointFromCentre(0.5, 0.5);
	        		
	        		System.out.println("Clicking at "+p.x+", "+p.y);
	        		if (foundID != 5) {
	        			System.out.println("Moving mouse");
	        			
	        			methods.moveMouse(p);
	        			Utils.wait(500);
	        			if (!methods.getStatus().equals("")) {
	        				clickedAd = true;
	        				System.out.println("Status checked");
	                		methods.mouse(p.x, p.y);
	                		break;
	        			}
	        		} else {
	        			clickedAd = true;
	        			methods.mouse(p);
	        			break;
	        		}
	        		
        		}
        	} else if (searchAdTries < 10) {
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
	
	private ElementBounds findAds(String... jqueryStrings) {
		ScriptMethods methods = adClicker.getScriptMethods();
		
		ElementBounds[] adsbygoogle = methods.getElements("$('.adsbygoogle').first().css('position', 'fixed').css('display', 'block').css('z-index', '99999999').css('left', '0px').css('top', '0px').show()");
		foundID = 0;
		if (adsbygoogle.length > 0) {
			ElementBounds bounds = adsbygoogle[0];
			ElementBounds[] iframe1 = methods.getElements("$('#google_ads_frame1')");
			if (iframe1.length > 0) {
				bounds.add(iframe1[0]);
				
				ElementBounds[] result = null;
				for (String s : jqueryStrings) {
					foundID++;

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
		
		return null;
	}
	
	private void handleAdTest(String url) {
		if (clickedAd) {
			DataEntry historyConfig = adClicker.getHistoryConfig();
		
			clickedAd = false;
			try {
				url = testAdURL(url).replace("https://", "http://");
				int id = url.lastIndexOf("?");
				if (id > 0)
					url = url.substring(0, id);
				
				DataEntry entry = historyConfig.getSingle("//*[@value='"+url+"']");
				if (entry == null) {
					entry = new HistoryEntry("", "", url);
					entry.add(new DataEntry("clicks", 1));
					historyConfig.add(entry);
				} else {
					int clicks = Integer.parseInt(entry.getSingle("//clicks").toString())+1;
					entry.set("//clicks", clicks);
					
					if (clicks <= MAX_CLICKS) {
						System.out.println("Already clicked this.");
						adClicker.getBotTab().stop();

						new Thread(new Runnable() {
							@Override
							public void run() {
								Utils.wait(2000);
								adClicker.loadBlog();
							}
							
						}).start();
					} else {
						System.out.println("This has been clicked "+clicks+" times");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
    
    private String testAdURL(String testURL) throws Exception {
		String url = "http://212.56.108.16/jacob/test_url.php";
		
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
}
