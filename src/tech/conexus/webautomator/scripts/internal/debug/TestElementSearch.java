package tech.conexus.webautomator.scripts.internal.debug;

import java.awt.Color;
import java.awt.Graphics;

import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.javafx.DefaultNetworkDelegate;

import tech.conexus.webautomator.Utils;
import tech.conexus.webautomator.script.Script;
import tech.conexus.webautomator.script.ScriptContext;
import tech.conexus.webautomator.script.ScriptMethods;
import tech.conexus.webautomator.script.dom.ElementBounds;
import tech.conexus.webautomator.shared.data.library.ScriptManifest;
import tech.conexus.webautomator.shared.tabs.Tab;
import tech.conexus.webautomator.shared.tabs.view.PaintListener;

public class TestElementSearch extends Script implements PaintListener {

	private Tab botTab;
	private boolean startExec;
	private ScriptMethods methods;
	private ElementBounds[] elements = null;
	
	private long mainFrame;

	public TestElementSearch(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	public void setDebugTab(Tab tab) {
		if (tab != null) {
			botTab = tab;
			
			/*botTab.getBrowserInstance().addLoadListener(new LoadAdapter() {
				@Override
				public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
					super.onFinishLoadingFrame(arg0);
					startExec = true;
				}
			});*/
			
			botTab.getTabView().addPaintListener(this);
			
			methods = new ScriptMethods(botTab);
		}
	}
	
	@Override
	public int tick() {
		Utils.wait(2000);
		if (botTab != null && !botTab.getBrowserInstance().isLoading()) {
			elements = methods.getElements(ScriptMethods.LINK_JQUERY);
		}
		
		return super.tick();
	}

	@Override
	public void onStart() {
		super.onStart();
		
		botTab = openTab("www.google.co.uk/news");
		botTab.getBrowserInstance().getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate() {
			@Override
			public void onBeforeURLRequest(BeforeURLRequestParams arg0) {
				super.onBeforeURLRequest(arg0);
				System.out.println(arg0.getURL());
				
			}
		});
		
		botTab.getBrowserInstance().addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
				super.onFinishLoadingFrame(arg0);
				if (arg0.isMainFrame()) {
					startExec = true;
					mainFrame = arg0.getFrameId();
				}
			}
		});
		
		botTab.getTabView().addPaintListener(this);
		botTab.setBlockJSMessages(false);
		methods = new ScriptMethods(botTab);
	}

	@Override
	public void onPaint(Graphics g) {
		if (elements != null) {
			synchronized (elements) {
				int x = (int) methods.getPageXOffset();
				int y = (int) methods.getPageYOffset();
				for (ElementBounds elem : elements) {
					g.setColor(Color.green);
					g.drawRect((int)(elem.x-x), (int)(elem.y-y), elem.width, elem.height);
				}
			}
		}
	}
}
