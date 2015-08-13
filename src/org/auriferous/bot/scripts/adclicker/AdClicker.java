package org.auriferous.bot.scripts.adclicker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.JBotFrame;
import org.auriferous.bot.gui.swing.script.JGuiListener;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.scripts.adclicker.gui.TaskManager;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabControlAdapter;
import org.auriferous.bot.tabs.TabListener;
import org.auriferous.bot.tabs.view.TabPaintListener;
import org.auriferous.bot.tabs.view.TabView;

import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

public class AdClicker extends Script implements TabPaintListener, JGuiListener{
	private static final int STAGE_SHUFFLES = 0;
	private static final int STAGE_URL = 1;
	private static final int STAGE_WAIT_ON_AD = 2;
	private static final int STAGE_SUB_CLICKS = 3;
	private static final int STAGE_DONE = 4;
	
	private Tab botTab;
	private ScriptMethods methods;
	
	private int curShuffles = 0;
	private int curSubClick = 0;
	private int searchAdTries = 0;

	private int taskStage = STAGE_SHUFFLES;
	private boolean startExec = false;
	
	private Task currentTask = null;
	private LinkedList<Task> tasks = new LinkedList<Task>();
	
	private ElementBounds debugElement = null;
	
	public AdClicker(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	private void executeTasks() {
		System.out.println("opening tab");
		
		botTab = openTab();
		botTab.getBrowserWindow().addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				if (event.isMainFrame()) {
					startExec = true;
				}
			}
		});
		
		botTab.getTabView().addTabPaintListener(this);
		
		getTabs().addTabControlListener(new TabControlAdapter() {
			@Override
			public void onTabClosed(Tab tab) {
				super.onTabClosed(tab);
				
				if (tab.equals(botTab)) {
					status = STATE_EXIT_FAILURE;
				}
			}
		});
		
		methods = new ScriptMethods(botTab);
		
		currentTask = tasks.poll();
		startExec = true;
		
		timer = System.currentTimeMillis();
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
	
	private String saveURL = "";
	private String blogURL = null;

	private long timer = 0;
	
	private void reset() {
		taskStage = STAGE_SHUFFLES;
		blogURL = null;
		saveURL = "";
		
		curShuffles = 0;
		curSubClick = 0;
		searchAdTries = 0;
	}
	
	@Override
	public int tick() {
		if (startExec) {
			if (taskStage == STAGE_SHUFFLES) {
				if (curShuffles < currentTask.shuffles) {
					curShuffles++;
					System.out.println("Doing shuffle "+curShuffles);
					
					Utils.wait(currentTask.timeInterval*1000);
					
					if (currentTask.url.endsWith("/"))
						botTab.loadURL(currentTask.url+"random");
					else
						botTab.loadURL(currentTask.url+"/random");
				} else
					taskStage++;
			}
			if (taskStage == STAGE_URL) {
	        	System.out.println("Started ad clicking");
	        	Utils.wait(2000);
	        	
	        	if (blogURL != null && !botTab.getBrowserWindow().getURL().equals(blogURL)) {
	        		System.out.println("Clicked ad successfully.");
	        		taskStage++;
	        	} else {
		        	ElementBounds adElement = findAds("$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");
	
		        	if (adElement != null) {
		        		blogURL = botTab.getBrowserWindow().getURL();
		        		
		        		adElement.width -= 35;
		        		
		        		debugElement = adElement;
			        	
		        		Point p = adElement.getRandomPointFromCentre(0.5, 0.5);
		        		
		        		methods.moveMouse(p);
			        	Utils.wait(500);
		        		searchAdTries = 0;
		        		
		        		System.out.println("Clicking at "+p.x+", "+p.y);
		        		methods.mouse(p.x, p.y);
		        	} else if (searchAdTries < 10) {
		        		searchAdTries++;
		        		System.out.println("Couldn't find ad on try "+searchAdTries+". Reloading page.");
		        		
		        		botTab.reload();
		        	} else if (searchAdTries == 10){
		        		System.out.println("Couldn't find ad. Terminating.");
		        		return STATE_EXIT_FAILURE;
		        	}
	        	}
			} 
			if (taskStage == STAGE_WAIT_ON_AD) {
				saveURL = botTab.getBrowserWindow().getURL();
				
				System.out.println("Saving URL "+saveURL);
				
				System.out.println("Now waiting on ad");
				Utils.wait(currentTask.timeOnAd*1000);
	
				taskStage++;
			}
			if (taskStage == STAGE_SUB_CLICKS) {
				if (curSubClick < currentTask.subClicks) {
					System.out.println("Clicking link in ad");

					ElementBounds randomLink = methods.getRandomLink(false);
					
					if (randomLink != null) {
						debugElement = randomLink;
						Point p = randomLink.getRandomPointFromCentre(0.5, 0.5);
			        	
			        	System.out.println("Clicking at "+p.x+", "+p.y);
			        	
			        	methods.mouse(p.x, p.y);
			        	
			        	System.out.println("Waiting 30 seconds");
			        	Utils.wait(30000);
			        	
			        	System.out.println("Going back to ad");
			        	botTab.loadURL(saveURL);
					} else if (searchAdTries < 5){
						searchAdTries++;
		        		System.out.println("Couldn't find link. Returning to ad.");
		        		
		        		botTab.loadURL(saveURL);
					}
		        	curSubClick++;
				}
				if (curSubClick == currentTask.subClicks)
					taskStage++;
			}
			if (taskStage == STAGE_DONE) {
				try {
					URL url = new URL(saveURL);
					String path = url.getFile().substring(0, url.getFile().lastIndexOf('/'));
					String base = url.getProtocol() + "://" + url.getHost() + path;
					
					String title = base.split("\\.")[1];
					
					System.out.println("clicked "+title+", base "+base+", from UK, same rules, ayysthetic.tk");
				} catch (Exception e) {
					e.printStackTrace();
				}
				reset();
				
				currentTask = tasks.poll();
				
				if (currentTask == null) {
					System.out.println("Finished!!");
					status = STATE_EXIT_SUCCESS;
				} else {
					startExec = true;
					return super.tick();
				}
			}
			timer = System.currentTimeMillis();
			startExec = false;
		} else {
			if (currentTask != null && System.currentTimeMillis()-timer >= 5000) {
				System.out.println("It's been 5 seconds. Forcing execution.");
				startExec = true;
			}
		}
		
		return super.tick();
	}

	@Override
	public void onStart() {
		new TaskManager(null, tasks);
	}

	@Override
	public void onPause() {
		
	}

	@Override
	public void onTerminate() {
		
	}
	
	@Override
	public void onPaint(Graphics g) {
		if (debugElement != null) {
			g.setColor(Color.green);
			g.drawRect(debugElement.x, debugElement.y, debugElement.width, debugElement.height);
		}
	}
	
	@Override
	public void onJMenuCreated(JMenu menu) {
		JMenuItem manageTasks = new JMenuItem(new MenuAction("Manage Tasks", 0));
		JMenuItem executeTasks = new JMenuItem(new MenuAction("Execute Tasks", 1));
		
		menu.add(manageTasks);
		menu.add(executeTasks);
	}

	@Override
	public void onTabViewCreated(Tab tab, TabView tabView) {
		// TODO Auto-generated method stub
		
	}
	
	class MenuAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private int actionID;
		
		public MenuAction(String text, int actionID) {
			super(text);
			this.actionID = actionID;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (actionID) {
				case 0: new TaskManager(null, tasks);
						break;
				case 1:	executeTasks();
						break;
			}
		}
	}
}
