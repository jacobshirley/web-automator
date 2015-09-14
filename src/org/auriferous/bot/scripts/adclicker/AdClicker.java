package org.auriferous.bot.scripts.adclicker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.auriferous.bot.Utils;
import org.auriferous.bot.data.DataEntry;
import org.auriferous.bot.data.config.Configurable;
import org.auriferous.bot.data.history.HistoryEntry;
import org.auriferous.bot.data.library.ScriptManifest;
import org.auriferous.bot.gui.swing.script.JScriptGuiListener;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.ScriptMethods.ClickType;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.scripts.adclicker.gui.SetSignatureFrame;
import org.auriferous.bot.scripts.adclicker.gui.TaskManager;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.view.PaintListener;

import com.teamdev.jxbrowser.chromium.AuthRequiredParams;
import com.teamdev.jxbrowser.chromium.BeforeRedirectParams;
import com.teamdev.jxbrowser.chromium.BeforeSendHeadersParams;
import com.teamdev.jxbrowser.chromium.BeforeSendProxyHeadersParams;
import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.HeadersReceivedParams;
import com.teamdev.jxbrowser.chromium.NetworkDelegate;
import com.teamdev.jxbrowser.chromium.RequestCompletedParams;
import com.teamdev.jxbrowser.chromium.RequestParams;
import com.teamdev.jxbrowser.chromium.ResponseStartedParams;
import com.teamdev.jxbrowser.chromium.SendHeadersParams;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;

public class AdClicker extends Script implements PaintListener, JScriptGuiListener, Configurable{
	private static final int DAYS_3_MS = 3*3600*24;
	
	private static final int STAGE_SHUFFLES = 0;
	private static final int STAGE_URL = 1;
	private static final int STAGE_TEST_CLICK = 2;
	private static final int STAGE_WAIT_ON_AD = 3;
	private static final int STAGE_SUB_CLICKS = 4;
	private static final int STAGE_FACEBOOK = 5;
	private static final int STAGE_DONE = 6;
	private static final int STAGE_NEXT_TASK = 7;
	
	private static final int SUB_CLICK_TIME = 10;
	private static final int SUB_CLICK_RANDOM_TIME = 4;
	
	private static final int MAX_WAIT_TIME = 20;
	
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
	public String currentTaskURL = "";
	
	private SetSignatureFrame setSigFrame = new SetSignatureFrame(this);
	
	private DataEntry taskConfig = new DataEntry("tasks");
	private DataEntry historyConfig = new DataEntry("click-history");
	private DataEntry taskHistoryConfig = new DataEntry("task-history");
	
	public DataEntry signatureConfig = new DataEntry("signature", "");
	
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
		
		if (botTab != null) {
			System.out.println("Loading URL "+curURL);
			botTab = openTab(curURL);
		} else
			botTab = openTab();
		
		botTab.getBrowserWindow().addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				if (event.isMainFrame()) {
					startExec = true;
				}
			}
		});
		
		botTab.getTabView().addPaintListener(this);
		
		final Browser browser = botTab.getBrowserWindow();
		browser.getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate() {
			@Override
			public void onBeforeURLRequest(BeforeURLRequestParams arg0) {
				handleAdTest(arg0.getURL());
			}
		});
		
		methods = new ScriptMethods(botTab);
	}
	
	private void handleAdTest(String url) {
		if (clickedAd) {
			clickedAd = false;
			try {
				url = testAdURL(url).replace("https://", "http://");
				int id = url.lastIndexOf("?");
				if (id > 0)
					url = url.substring(0, id);
				
				DataEntry entry = historyConfig.getSingle("//*[@value='"+url+"']");
				if (entry != null)
					historyConfig.add(new HistoryEntry("", "", url));
				else {
					long timeStamp = new HistoryEntry(entry).getTimeStamp();
					
					if (System.currentTimeMillis()-timeStamp <= DAYS_3_MS) {
						System.out.println("Already clicked this.");
						botTab.stop();

						taskStage = STAGE_TEST_CLICK;
						startExec = true;
					} else {
						System.out.println("Not already clicked this "+timeStamp);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
    
    private String testAdURL(String testURL) throws Exception {
		String url = "http://212.56.108.16/jacob/test_url.php";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "url="+URLEncoder.encode(testURL, "UTF-8");
		
		// Send post request
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
	
	private int foundID = 0;
	
	private ElementBounds findAds(String... jqueryStrings) {
		ElementBounds[] adsbygoogle = methods.getElements("$('.adsbygoogle').css('position', 'fixed').css('display', 'block').css('z-index', '99999999').css('left', '0px').css('top', '0px').show()");
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
		
		return signatureConfig.getValue().toString().replace("$title", title).replace("$base", base);
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
	
	private boolean clickedAd = false;
	private boolean clickedAdSuccess = false;
	
	private boolean tickAdClicking() {
		System.out.println("Started ad clicking");
    	Utils.wait(2000);
    	
    	String url = botTab.getURL();
    	
    	if (blogURL != null && !url.contains(getBaseURL(currentTaskURL))) {
    		System.out.println("Clicked ad successfully.");
    		taskStage = STAGE_WAIT_ON_AD;
    		clickedAdSuccess = true;
    		
    		return false;
    	} else {
    		currentTaskURL = url;
    		
        	ElementBounds adElement = findAds("$('.rh-title').find('a');", "$('#ad_iframe');", "$('#google_image_div').find('img');", "$('#bg-exit');", "$('#google_flash_embed');");

        	if (foundID == 1) {
        		ElementBounds adLink = methods.getRandomElement("$('a[href*=\"adurl=\"]')");
        		if (adLink != null) {
        			String adhref = adLink.getDOMElement().get("href").getString().split("adurl=")[1];
        			System.out.println("found "+adhref);
        		}
        	}
        	searchAdTries++;
        	if (adElement != null) {
        		blogURL = botTab.getURL();
        		
        		adElement.width -= 35;
        		
        		debugElement = adElement;
        		
        		
        		for (int i = 0; i < 10; i++) {
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
        	} else if (searchAdTries == 10){
        		System.out.println("Couldn't find ad. Next task...");
        		
        		taskStage = STAGE_NEXT_TASK;
        	}
    	}
    	
    	return true;
	}
	
	private boolean tickTestClick() {
		if (!clickedAdSuccess) {
			blogURL = null;
			clickedAdSuccess = false;
			Utils.wait(2000);
			System.out.println("Loading url");
			loadURL();
			
			taskStage = STAGE_URL;
			return true;
		}
		return false;
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
	        	Utils.wait((SUB_CLICK_TIME*1000)+Utils.random(0, SUB_CLICK_RANDOM_TIME*1000));
	        	
	        	System.out.println("Going back to ad "+saveURL);
	        	botTab.loadURL(saveURL);
	        	
	        	curSubClick++;
			} else if (searchAdTries < 2){
				searchAdTries++;
        		System.out.println("Couldn't find link on try "+searchAdTries+"/2. Returning to ad to try again.");
        		
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
		
		return false;
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
			botTab.alert("Finished!");
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
			//if (!loading || forceExec) {
			if (startExec || forceExec) {
				startExec = false;
				
				curURL = botTab.getBrowserWindow().getURL();
				
				String title = botTab.getTitle();
				if (title.contains("Not found.")) {
					System.out.println("Page not found. Next task...");
					taskStage = STAGE_NEXT_TASK;
				}
				
				try {
					switch (taskStage){
					case STAGE_SHUFFLES:
						if (tickShuffles())
							break;
					case STAGE_URL:
						if (tickAdClicking())
							break;
					case STAGE_TEST_CLICK:
						if (tickTestClick())
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
					System.out.println("There was an error. Skipping task");
					taskStage = STAGE_NEXT_TASK;
					forceExec = true;
					timer = System.currentTimeMillis();
					return super.tick();
				}
				
				forceExec = false;
				
				timer = System.currentTimeMillis();
			} else if (currentTask != null && System.currentTimeMillis()-timer >= MAX_WAIT_TIME*1000) {
				System.out.println("It's been "+MAX_WAIT_TIME+" seconds. Forcing execution.");
				forceExec = true;
				taskStage++;
				timer = System.currentTimeMillis();
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
	public void load(DataEntry configEntries) {
		List<DataEntry> l = configEntries.get("//tasks");
		
		for (DataEntry tasks : l) {
			taskConfig = tasks;
			
			for (DataEntry taskEntry : taskConfig.getChildren()) {
				Task t = new Task(taskEntry);
				
				this.tasks.add(t);
			}
		}
		
		l = configEntries.get("//"+historyConfig.getKey());
		for (DataEntry history : l) {
			historyConfig = history;
		}
		
		l = configEntries.get("//"+taskHistoryConfig.getKey());
		for (DataEntry history : l) {
			taskHistoryConfig = history;
		}
		
		l = configEntries.get("//"+signatureConfig.getKey());
		for (DataEntry signature : l) {
			signatureConfig = signature;
		}

		setSigFrame.setText(signatureConfig.getValue().toString());
		System.out.println(historyConfig.contains("//*[@value='http://www.manageengine.com/products/applications_manager/applications-monitoring-features.html']"));
		System.out.println(historyConfig.get("//*[@value='"+"http://www.manageengine.com/products/applications_manager/applications-monitoring-features.html".replace("https://", "http://")+"']"));
	}

	@Override
	public void save(DataEntry root) {
		taskConfig.clear();
		
		for (Task t : tasks) {
			taskConfig.add(new TaskConfigEntry(t));
		}
		
		root.add(signatureConfig, true);
		root.add(taskConfig, true);
		root.add(historyConfig, true);
		root.add(taskHistoryConfig, true);
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		
	}
}