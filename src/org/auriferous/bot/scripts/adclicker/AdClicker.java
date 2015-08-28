package org.auriferous.bot.scripts.adclicker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.auriferous.bot.Utils;
import org.auriferous.bot.config.WritableEntry;
import org.auriferous.bot.config.Configurable;
import org.auriferous.bot.config.ConfigurableEntry;
import org.auriferous.bot.config.library.ScriptManifest;
import org.auriferous.bot.gui.swing.script.JScriptGuiListener;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.ScriptMethods.ClickType;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.scripts.adclicker.gui.SetSignatureFrame;
import org.auriferous.bot.scripts.adclicker.gui.TaskManager;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.view.TabPaintListener;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class AdClicker extends Script implements TabPaintListener, JScriptGuiListener, Configurable{
	private static final int STAGE_SHUFFLES = 0;
	private static final int STAGE_URL = 1;
	private static final int STAGE_WAIT_ON_AD = 2;
	private static final int STAGE_SUB_CLICKS = 3;
	private static final int STAGE_FACEBOOK = 4;
	private static final int STAGE_DONE = 5;
	private static final int STAGE_NEXT_TASK = 6;
	
	private static final int SUB_CLICK_TIME = 10;
	private static final int SUB_CLICK_RANDOM_TIME = 4;
	
	private static final int MAX_WAIT_TIME = 10;
	
	private Tab botTab;
	private ScriptMethods methods;
	
	private int curShuffles = 0;
	private int curSubClick = 0;
	private int searchAdTries = 0;

	private int taskStage = STAGE_SHUFFLES;
	private boolean startExec = false;
	private boolean forceExec = false;
	private boolean skipTask = false;
	
	private Task currentTask = null;
	private LinkedList<Task> tasks = new LinkedList<Task>();
	
	private ElementBounds debugElement = null;
	
	private String saveURL = "";
	private String blogURL = null;

	private long timer = 0;
	public String currentSignature = "";
	
	private SetSignatureFrame setSigFrame = new SetSignatureFrame(this);
	
	private ConfigurableEntry<String,String> taskConfig = new WritableEntry<String,String>("tasks");
	
	public AdClicker(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	private void executeTasks() {
		resetTab();
		
		currentTask = tasks.poll();
		startExec = true;
		
		timer = System.currentTimeMillis();
		
		System.out.println("Starting task "+currentTask.url);
		
		reset();
	}
	
	private void setSignature() {
		setSigFrame.setVisible(true);
	}
	
	private String curURL;
	
	private void resetTab() {
		System.out.println("Opening tab");
		
		if (botTab != null)
			botTab = openTab(curURL);
		else
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
		
		methods = new ScriptMethods(botTab);
	}
	
	private int foundID = 0;
	
	private ElementBounds findAds(String... jqueryStrings) {
		ElementBounds[] adsbygoogle = methods.getElements("$('.adsbygoogle').css('position', 'fixed').css('display', 'block').css('z-index', '99999999').css('left', '0px').css('top', '0px').show()");
		foundID = 0;
		if (adsbygoogle != null) {
			System.out.println("Found basic ad");
			
			ElementBounds bounds = adsbygoogle[0];
			ElementBounds[] iframe1 = methods.getElements("$('#google_ads_frame1')");
			if (iframe1 != null) {
				bounds.add(iframe1[0]);
				
				ElementBounds[] result = null;
				for (String s : jqueryStrings) {
					foundID++;
					
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
	
	private void reset() {
		taskStage = STAGE_SHUFFLES;
		blogURL = null;
		saveURL = "";
		
		curShuffles = 0;
		curSubClick = 0;
		searchAdTries = 0;
	}
	
	private String getBaseURL(String urlString) {
		try {
			URL url = new URL(urlString);
			
			String path = url.getFile().substring(0, url.getFile().lastIndexOf('/'));
			String base = url.getProtocol() + "://" + url.getHost() + path;
			
			return base;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String compileSignature(String urlString) {
		String base = getBaseURL(urlString);
		String title = base.split("\\.")[1];
		
		return currentSignature.replace("$title", title).replace("$base", base);
	}
	
	private boolean tickShuffles() {
		if (curShuffles < currentTask.shuffles) {
			curShuffles++;
			System.out.println("Doing shuffle "+curShuffles);
			
			Utils.wait(currentTask.timeInterval*1000);
			
			loadURL();
		} else {
			taskStage = STAGE_URL;
			return false;
		}
		
		return true;
	}
	
	private boolean tickAdClicking() {
		System.out.println("Started ad clicking");
    	Utils.wait(2000);
    	
    	String url = botTab.getURL();
    	
    	if (blogURL != null && !url.contains(getBaseURL(currentTask.url))) {
    		System.out.println("Clicked ad successfully.");
    		taskStage = STAGE_WAIT_ON_AD;
    		
    		return false;
    	} else {
        	ElementBounds adElement = findAds("$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");

        	if (adElement != null) {
        		blogURL = botTab.getURL();
        		
        		adElement.width -= 35;
        		
        		debugElement = adElement;
	        	
        		Point p = adElement.getRandomPointFromCentre(0.5, 0.5);
        		searchAdTries = 0;
        		
        		System.out.println("Clicking at "+p.x+", "+p.y);
        		if (foundID != 5) {
        			System.out.println("Moving mouse");
        			
        			methods.moveMouse(p);
        			Utils.wait(500);
        			if (!methods.getStatus().equals("")) {
        				System.out.println("Status checked");
                		methods.mouse(p.x, p.y);
        			}
        		} else {
        			System.out.println("Clicking here");
        			
        			methods.mouse(p);
        		}
        	} else if (searchAdTries < 10) {
        		searchAdTries++;
        		System.out.println("Couldn't find ad on try "+searchAdTries+"/10. Reloading page.");
        		
        		botTab.reload();
        	} else if (searchAdTries == 10){
        		System.out.println("Couldn't find ad. Next task...");
        		
        		taskStage = STAGE_NEXT_TASK;
        	}
    	}
    	
    	return true;
	}
	
	private boolean tickWaitOnAd() {
		saveURL = botTab.getURL();
		
		System.out.println("Saving URL "+saveURL);
		
		System.out.println("Now waiting on ad with random 5 seconds");
		Utils.wait((currentTask.timeOnAd*1000) + Utils.random(5000));

		taskStage = STAGE_SUB_CLICKS;
		
		return false;
	}
	
	private boolean tickSubClicks() {
		if (curSubClick < currentTask.subClicks) {
			Utils.wait(2000);
			System.out.println("Clicking link in ad");

			ElementBounds randomLink = methods.getRandomClickable(false);
			
			if (randomLink != null) {
				debugElement = randomLink;
				Point p = randomLink.getRandomPointFromCentre(0.5, 0.5);
	        	
	        	System.out.println("Clicking at "+p.x+", "+p.y);
	        	
	        	methods.mouse(p.x, p.y);
	        	
	        	System.out.println("Waiting 10 seconds + random time (0 - 4 seconds)");
	        	Utils.wait((int)((SUB_CLICK_TIME*1000)+Utils.random(0, SUB_CLICK_RANDOM_TIME*1000)));
	        	
	        	System.out.println("Going back to ad");
	        	botTab.loadURL(saveURL);
	        	
	        	curSubClick++;
			} else if (searchAdTries < 5){
				searchAdTries++;
        		System.out.println("Couldn't find link on try "+searchAdTries+"/5. Returning to ad to try again.");
        		
        		botTab.loadURL(saveURL);
			}
		} else {
			taskStage = STAGE_FACEBOOK;
			return false;
		}
		
		return true;
	}
	
	public boolean tickFBPostComment() {
		if (!currentTask.fbLink.equals("")) {
			final String url = saveURL;
			final Tab fbTab = openTab(currentTask.fbLink);
			
			fbTab.getBrowserWindow().addLoadListener(new LoadAdapter() {
				@Override
				public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
					if (arg0.isMainFrame()) {
						Utils.wait(5000);
						System.out.println("On Facebook page!!!!");
						
						ScriptMethods fbMethods = new ScriptMethods(fbTab);
						
						ElementBounds fbFoto = fbMethods.getRandomElement("$('.UFIReplyActorPhotoWrapper');");
						
						if (fbFoto != null) {
							System.out.println("Found Facebook photo");
							
							Point p = fbFoto.getRandomPointFromCentre(0.5, 0.5);
							
							p.x += 150;
							
							fbMethods.mouse(p, ClickType.LCLICK);
							Utils.wait(500);
							fbMethods.mouse(p, ClickType.LCLICK);
							fbMethods.type(compileSignature(url));
						}
					}
				}
			});
		}
		
		taskStage = STAGE_DONE;
		
		return true;
	}
	
	public boolean tickTaskDone() {
		System.out.println("Finished current task");
		
		System.out.println(compileSignature(saveURL));
		
		taskStage = STAGE_NEXT_TASK;
		
		return false;
	}
	
	private boolean tickNextTask() {
		reset();
		
		currentTask = tasks.poll();
		
		if (currentTask != null)
			System.out.println("Starting next task "+currentTask.url);
		
		if (currentTask == null) {
			System.out.println("Finished all tasks");
			status = STATE_EXIT_SUCCESS;
		} else {
			startExec = true;
		}
		
		return true;
	}
	
	private void loadURL() {
		if (currentTask != null) {
			if (currentTask.url.endsWith("/"))
				botTab.loadURL(currentTask.url+"random");
			else
				botTab.loadURL(currentTask.url+"/random");
		}
	}
	
	@Override
	public int tick() {
		if (botTab != null) {
			boolean disposed = botTab.getBrowserWindow().isDisposed();
			if (disposed) {
				getTabs().closeTab(botTab);
				System.out.println("Apparently disposed. Opening new tab.");
				resetTab();
				return super.tick();
			}
			
			if (skipTask) {
				System.out.println("Skipping task...");
				skipTask = false;
				taskStage = STAGE_NEXT_TASK;
			}
			
			boolean loading = botTab.getBrowserWindow().isLoading();
			if (!loading) {
				if (startExec || forceExec) {
					startExec = false;
					
					curURL = botTab.getBrowserWindow().getURL();
					
					String title = botTab.getTitle();
					if (title.equals("Not found.")) {
						System.out.println("Page not found. Next task...");
						taskStage = STAGE_NEXT_TASK;
					}
					
					curURL = botTab.getBrowserWindow().getURL();
					
					try {
						switch (taskStage){
						case STAGE_SHUFFLES:
							if (tickShuffles())
								break;
						case STAGE_URL:
							if (forceExec) {
								loadURL();
								break;
							} else
								if (tickAdClicking())
									break;
						case STAGE_WAIT_ON_AD:
							if (tickWaitOnAd())
								break;
						case STAGE_SUB_CLICKS:
							if (tickSubClicks())
								break;
						case STAGE_FACEBOOK:
							if (tickFBPostComment())
								break;
						case STAGE_DONE:
							if (tickTaskDone())
								break;
						case STAGE_NEXT_TASK:
							if (tickNextTask())
								break;
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("There was an error. Skipping task "+currentTask.url);
						taskStage = STAGE_NEXT_TASK;
						forceExec = true;
						
						return super.tick();
					}
					
					forceExec = false;
					
					timer = System.currentTimeMillis();
				} else {
					if (currentTask != null && System.currentTimeMillis()-timer >= MAX_WAIT_TIME*10000) {
						System.out.println("It's been "+MAX_WAIT_TIME+" seconds. Forcing execution.");
						forceExec = true;
					}
				}
			}
		}
		return super.tick();
	}

	@Override
	public void onStart() {
		new TaskManager(tasks, taskConfig);
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onResume() {
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
		JMenuItem setSignature = new JMenuItem(new MenuAction("Signature", 2));
		JMenuItem manageTasks = new JMenuItem(new MenuAction("Manage Tasks", 0));
		JMenuItem executeTasks = new JMenuItem(new MenuAction("Execute Tasks", 1));
		JMenuItem skipTask = new JMenuItem(new MenuAction("Skip Task", 3));
		
		menu.add(setSignature);
		menu.add(manageTasks);
		menu.add(executeTasks);
		menu.addSeparator();
		menu.add(skipTask);
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
				case 0: new TaskManager(tasks, taskConfig);
						break;
				case 1:	executeTasks();
						break;
				case 2:	setSignature();
						break;
				case 3:	skipTask = true;
						break;
			}
		}
	}

	@Override
	public void loadDefault() {
	}

	@Override
	public void load(ConfigurableEntry configEntries) {
		Object s = configEntries.get("//signature", "");
		
		currentSignature = (String) s;
		setSigFrame.setText(currentSignature);
		
		List<ConfigurableEntry> l = configEntries.get("//tasks");
		for (ConfigurableEntry tasks : l) {
			taskConfig = tasks;
			for (ConfigurableEntry<Object,Object> taskEntry : taskConfig.getChildren()) {
				Task t = new Task(taskEntry);
				
				this.tasks.add(t);
			}
		}
	}

	@Override
	public ConfigurableEntry<String,String> getConfiguration() {
		ConfigurableEntry<String,String> root = new WritableEntry("config");
		
		root.getChildren().add(new WritableEntry<String,String>("signature", currentSignature));
		
		taskConfig = new WritableEntry<String,String>("tasks");
		
		for (Task t : tasks) {
			taskConfig.getChildren().add(new TaskConfigEntry(t));
		}
		
		root.getChildren().add(taskConfig);
		
		return root;
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		
	}
}