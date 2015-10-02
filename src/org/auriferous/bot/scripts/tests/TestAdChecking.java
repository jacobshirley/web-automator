package org.auriferous.bot.scripts.tests;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.data.library.ScriptManifest;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.callbacks.JSCallback;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.tabs.Tab;

import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;

public class TestAdChecking extends Script {
	private static final String URL = "dexsus.tk/random";
	
	private static final String ASWIFT = "$('ins[id^=\"aswift_\"][id$=\"_anchor\"]').css('position', 'fixed').css('display', 'block').css('z-index', '99999999').css('left', '0px').css('top', '0px').show()";
	
	private static final String[] AD_ELEMENT_SEARCHES = new String[] {ASWIFT};
	
	private Tab botTab = null;
	private ScriptMethods methods;
	private boolean exec = false;
	private boolean pageLoading = false;
	
	public TestAdChecking(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onStart() {
		this.botTab = openTab(URL);
		this.methods = new ScriptMethods(botTab);
		this.botTab.getBrowserInstance().addLoadListener(new LoadAdapter() {
			@Override
			public void onDocumentLoadedInFrame(FrameLoadEvent arg0) {
				exec = true;
			}
		});
		
		final Browser browser = botTab.getBrowserInstance();
		browser.getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate() {
			@Override
			public void onBeforeURLRequest(BeforeURLRequestParams event) {
				String url = event.getURL();

				if (url.contains("&adurl=")) {
					pageLoading = true;
					try {
						System.out.println("Getting url "+Utils.getBaseURL(URLDecoder.decode(url.split("&adurl=")[1], "UTF-8")));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	@Override
	public int tick() {
		if (exec) {
			exec = false;
			
			/*ElementBounds ad = findAds("$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");
			
			if (ad != null) {
				System.out.println(getBaseAdURL());
				//methods.clickElement(ad);
			}*/
			botTab.loadURL(URL);
		}
		return super.tick();
	}
	
	private String getBaseAdURL() {
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
		return Utils.getBaseURL(url);
	}
	
	private void removeAllElementsButOne(String jquery) {
		methods.execJS("removeAllButOne("+jquery+");");
	}
	
	private ElementBounds findAds(String... jqueryStrings) {
		List<String> randomList = Arrays.asList(AD_ELEMENT_SEARCHES);
		Collections.shuffle(randomList);
		
		for (String search : randomList) {
			ElementBounds[] rootAds = methods.getElements(search);
			
			if (rootAds.length > 0) {
				//moveElements(botTab.getBrowserInstance(), methods, search, "$('body')");
				removeAllElementsButOne(search);
			
				/*for (String search2 : randomList) {
					if (!search2.equals(search)) {
						System.out.println("Removing "+search2);
						removeAllElements(botTab.getBrowserInstance(), methods, search2);
					}
				}*/
				ElementBounds bounds = rootAds[0];
				ElementBounds[] iframe1 = methods.getElements("$('iframe[id^=\"google_ads_frame\"]')");
				
				if (iframe1.length > 0) {
					//System.out.println("Found google_ads_frame");
					bounds.add(iframe1[0]);
					
					ElementBounds[] result = null;
					for (String s : jqueryStrings) {
						result = methods.getElements(s);
						if (result.length > 0) {
							//System.out.println("Found "+s);
							
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
}
