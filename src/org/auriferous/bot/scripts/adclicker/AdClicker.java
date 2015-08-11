package org.auriferous.bot.scripts.adclicker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.BotGUI;
import org.auriferous.bot.script.ElementBounds;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.scripts.adclicker.gui.TaskManager;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabPaintListener;

import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

public class AdClicker extends Script implements TabPaintListener{
	private static final int STAGE_SHUFFLES = 0;
	private static final int STAGE_URL = 1;
	private static final int STAGE_WAIT_ON_AD = 2;
	private static final int STAGE_SUB_CLICKS = 3;
	private static final int STAGE_DONE = 4;
	
	private int curShuffles = 0;
	private int curSubClick = 0;
	private int searchAdTries = 0;

	private int taskStage = STAGE_SHUFFLES;
	private boolean startExec = false;
	
	private Task currentTask = null;
	private LinkedList<Task> tasks = new LinkedList<Task>();
	
	public AdClicker(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	private Tab botTab;
	private ScriptMethods methods;
	
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
		
		botTab.addTabPaintListener(this);
		
		methods = new ScriptMethods(botTab);
		
		currentTask = tasks.poll();
		startExec = true;
	}
	
	private void reset() {
		taskStage = STAGE_SHUFFLES;
		
		curShuffles = 0;
		curSubClick = 0;
		searchAdTries = 0;
	}
	
	private ElementBounds findAds(String... jqueryStrings) {
		ElementBounds result = null;
		for (String s : jqueryStrings) {
			System.out.println("Trying "+s);
			result = methods.getRandomElement(s);
			if (result != null)
				break;
		}
		return result;
	}
	
	private String saveURL = "";
	private String urlTitle = "";
	
	private long timer = 0;
	
	@Override
	public int tick() {
		ElementBounds adElement = null;
		if (currentTask != null) {
			if (startExec) {
				if (taskStage == STAGE_SHUFFLES) {
					if (curShuffles < currentTask.shuffles) {
						Utils.wait(currentTask.timeInterval*1000);
						curShuffles++;
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
		        	
	        		adElement = findAds("$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");
	        		boolean done = false;
		        	if (adElement != null) {
		        		adElement.width -= 35;
		        		
		        		debugElement = adElement;
			        	
			        	for (int j = 0; j < 10; j++) {
			        		Point p = adElement.getRandomPointFromCentre(0.5, 0.5);
			        		
			        		methods.moveMouse(p);
				        	Utils.wait(500);
				        	if (true) {
				        		searchAdTries = 0;
				        		
				        		System.out.println("Clicking at "+p.x+", "+p.y);
				        		methods.mouse(p.x, p.y);
				        		taskStage++;
				        		done = true;
				        		break;
				        	}
			        	}
		        	}
		        	if (!done && searchAdTries < 10) {
		        		searchAdTries++;
		        		System.out.println("Couldn't find ad. Reloading page.");
		        		
		        		botTab.reload();
		        	}
				} else if (taskStage == STAGE_WAIT_ON_AD) {
					saveURL = botTab.getBrowserWindow().getURL();
					
					System.out.println("Saving URL "+saveURL);
					
					System.out.println("Now waiting on ad");
					Utils.wait(currentTask.timeOnAd*1000);
		
					taskStage++;
				}
				if (taskStage == STAGE_SUB_CLICKS) {
					if (curSubClick < currentTask.subClicks) {
						System.out.println("Clicking link in ad");
						urlTitle = botTab.getTitle();
						
						ElementBounds randomLink = methods.getRandomLink();
						
						if (randomLink != null) {
							debugElement = randomLink;
							Point p = randomLink.getRandomPoint();
				        	
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
					reset();
	
					System.out.println("clicked "+urlTitle+" base "+saveURL+" from UK, same rules, ayysthetic.tk");
					
					currentTask = tasks.poll();
					
					if (currentTask == null)
						status = STATE_EXIT_SUCCESS;
					else
						startExec = true;
				} else startExec = false;
			} else {
				/*if (System.currentTimeMillis() < timer) {
					botTab.reload();
				}*/
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
	public void onGUICreated(JMenu menu) {
		JMenuItem manageTasks = new JMenuItem(new MenuAction("Manage Tasks", 0));
		JMenuItem executeTasks = new JMenuItem(new MenuAction("Execute Tasks", 1));
		
		menu.add(manageTasks);
		menu.add(executeTasks);
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

	private ElementBounds debugElement = null;
	
	@Override
	public void onPaint(Graphics g) {
		if (debugElement != null) {
			g.setColor(Color.green);
			g.drawRect(debugElement.x, debugElement.y, debugElement.width, debugElement.height);
		}
	}
}
