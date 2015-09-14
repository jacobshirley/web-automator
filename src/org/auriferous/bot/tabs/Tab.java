package org.auriferous.bot.tabs;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.auriferous.bot.Utils;
import org.auriferous.bot.data.history.HistoryConfig;
import org.auriferous.bot.data.history.HistoryEntry;
import org.auriferous.bot.tabs.view.TabView;

import com.teamdev.jxbrowser.chromium.AuthRequiredParams;
import com.teamdev.jxbrowser.chromium.BeforeRedirectParams;
import com.teamdev.jxbrowser.chromium.BeforeSendHeadersParams;
import com.teamdev.jxbrowser.chromium.BeforeSendProxyHeadersParams;
import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.Callback;
import com.teamdev.jxbrowser.chromium.CertificateErrorParams;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.CustomProxyConfig;
import com.teamdev.jxbrowser.chromium.DefaultLoadHandler;
import com.teamdev.jxbrowser.chromium.HeadersReceivedParams;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.LoadHandler;
import com.teamdev.jxbrowser.chromium.LoadParams;
import com.teamdev.jxbrowser.chromium.NetworkDelegate;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.RequestCompletedParams;
import com.teamdev.jxbrowser.chromium.RequestParams;
import com.teamdev.jxbrowser.chromium.ResponseStartedParams;
import com.teamdev.jxbrowser.chromium.SendHeadersParams;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.TitleEvent;
import com.teamdev.jxbrowser.chromium.events.TitleListener;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;

public class Tab {
	private static final List<Browser> BROWSER_INSTANCES = new ArrayList<Browser>();
	
	private static final BrowserContext DEFAULT_CONTEXT = new BrowserContext("test");
	
	private int id;
	private String originalURL;
	
	private Browser browser;
	
	private List<TabListener> tabListeners = new LinkedList<TabListener>();
	private Stack<TabCallback> tabCallbacks = new Stack<TabCallback>();

	private TabView tabView;
	private Tabs parent;
	
	private HistoryConfig history;
	
	public Tab(final Tabs parent, String url, final HistoryConfig history) {
		this.id = -1;
		this.parent = parent;
		this.history = history;
		
		this.browser = new Browser(new BrowserContext("test"+(10*Math.random())));
		
		System.out.println("Finished");
		this.browser.getPreferences().setLocalStorageEnabled(true);
		
		
		
		BROWSER_INSTANCES.add(browser);
		
		browser.addTitleListener(new TitleListener() {
            @Override
			public void onTitleChange(TitleEvent event) {
            	for (TabListener listener : tabListeners) 
					listener.onTitleChange(event.getTitle());
            }
        });
		
		loadURL(url);
		
		this.browser.registerFunction("tabCallback", new BrowserFunction() {
			@Override
		    public JSValue invoke(JSValue... args) {
		    	TabCallback callback = tabCallbacks.get(0);
		    	
		    	if (callback != null) {
			    	Object[] oArgs = new Object[args.length];
			    	
			    	int c = 0;
			        for (JSValue arg : args) {
			            if (arg.isBoolean())
			            	oArgs[c] = arg.getBoolean();
			            else if (arg.isNumber())
			            	oArgs[c] = arg.getNumber();
			            else if (arg.isString())
			            	oArgs[c] = arg.getString();
			            else if (arg.isNull() || arg.isUndefined())
			            	oArgs[c] = null;
			            else 
			            	oArgs[c] = (JSObject)arg;
			            c++;
			        }
			        Object returned = callback.onInvoke(oArgs);
			       
			        if (returned instanceof Number)
			        	return JSValue.create(Double.parseDouble(returned.toString()));
			        else if (returned instanceof Boolean)
			        	return JSValue.create((Boolean)returned);
			        else if (returned instanceof String)
			        	return JSValue.create((String)returned);
			        
			        return JSValue.createNull();
		    	}
		    	return JSValue.createNull();
	    	}
		});
		
		this.browser.registerFunction("println", new BrowserFunction() {
			@Override
		    public JSValue invoke(JSValue... args) {
		    	System.out.println("JAVASCRIPT: "+args[0].getString());
		    	return JSValue.createNull();
	    	}
		});
		
		this.browser.setPopupHandler(new PopupHandler() {
		    @Override
			public PopupContainer handlePopup(final PopupParams params) {
		    	
		        return new PopupContainer() {
					@Override
					public void insertBrowser(Browser arg0,
							java.awt.Rectangle arg1) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								Utils.wait(100);
								loadURL(params.getURL());
							}
						}).start();
					}
		        };
		    }
		});
		
		this.browser.addLoadListener(new LoadAdapter() {
			@Override
			public void onDocumentLoadedInMainFrame(LoadEvent arg0) {
				super.onDocumentLoadedInMainFrame(arg0);
				
				Browser b = arg0.getBrowser();
				history.addEntry(new HistoryEntry(System.currentTimeMillis(), "", b.getTitle(), b.getURL()));
			}
		});
	}
	
	public void goBack() {
		this.browser.goBack();
	}
	
	public void goForward() {
		this.browser.goForward();
	}
	
	public void stop() {
		this.browser.stop();
	}
	
	public void alert(String message) {
		browser.executeJavaScript("alert('"+message+"');");
	}
	
	public void pushCallback(TabCallback callback) {
		tabCallbacks.push(callback);
	}
	
	public void popCallback() {
		tabCallbacks.pop();
	}
	
	public void loadURL(String url) {
		if (!this.browser.isDisposed()) {
			this.originalURL = url;
		
			this.browser.loadURL(url);
		
			for (TabListener listener : tabListeners) 
				listener.onTabUpdating();
		}
	}
	
	public void reload() {
		if (!this.browser.isDisposed()) {
			this.browser.reload();
		
			for (TabListener listener : tabListeners) 
				listener.onTabReloaded();
		}
	}
	
	public String getTitle() {
		if (!browser.isDisposed())
			return browser.getTitle();
		return "";
	}
	
	public String getURL() {
		if (!browser.isDisposed())
			return browser.getURL();
		return "";
	}
	
	public String getOriginalURL() {
		return originalURL;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public Browser getBrowserWindow() {
		return browser;
	}
	
	public void setTabView(TabView tabView) {
		this.tabView = tabView;
	}
	
	public TabView getTabView() {
		return tabView;
	}
	
	public void addTabListener(TabListener tabListener) {
		this.tabListeners.add(tabListener);
	}
	
	public void removeTabListener(TabListener tabListener) {
		this.tabListeners.remove(tabListener);
	}
	
	static {
		Thread t = new Thread(new Runnable() {
		    @Override
			public void run() {
		    	for (Browser browser : BROWSER_INSTANCES) {
		    		if (!browser.isDisposed())
		    			browser.dispose();
		    	}
		    }
		});
		t.setDaemon(true);
		
		Runtime.getRuntime().addShutdownHook(t);
	}
}
