package org.auriferous.bot.scripts.adclicker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.auriferous.bot.Utils;
import org.auriferous.bot.data.DataEntry;
import org.auriferous.bot.data.config.Configurable;
import org.auriferous.bot.data.history.HistoryEntry;
import org.auriferous.bot.data.library.ScriptManifest;
import org.auriferous.bot.gui.swing.script.JScriptGui;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.scripts.adclicker.gui.SetSignatureFrame;
import org.auriferous.bot.scripts.adclicker.gui.TaskManager;
import org.auriferous.bot.scripts.adclicker.states.TaskNextState;
import org.auriferous.bot.scripts.adclicker.states.events.Events;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.view.PaintListener;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class AdClicker extends Script implements PaintListener, JScriptGui, Configurable{
	private static final int MAX_WAIT_TIME = 10;
	
	private Tab botTab;
	private ScriptMethods methods;
	private boolean skipTask = false;
	
	private Task currentTask = null;
	private LinkedList<Task> tasks = new LinkedList<Task>();
	
	private ElementBounds debugElement = null;
	
	private SetSignatureFrame setSigFrame = new SetSignatureFrame(this);
	
	private DataEntry taskConfig = new DataEntry("tasks");
	private DataEntry historyConfig = new DataEntry("click-history");
	private DataEntry taskHistoryConfig = new DataEntry("task-history");
	
	public DataEntry signatureConfig = new DataEntry("signature", "");
	
	private FSM stateMachine = new FSM();
	
	private long loadTimer = System.currentTimeMillis();
	
	private long mainFrameID = -1;

	private String curURL = "";
	
	private LoadAdapter loader = new LoadAdapter() {
		@Override
		public void onFinishLoadingFrame(FinishLoadingEvent event) {
			if (event.isMainFrame()) {
				System.out.println("page loaded");
				
				mainFrameID = event.getFrameId();
				curURL = botTab.getURL();
				loadTimer = System.currentTimeMillis();
				stateMachine.pushEvent(Events.EVENT_PAGE_LOADED);
			}
		}
	};
	
	public AdClicker(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	private void executeTasks() {
		executeTasks.setEnabled(false);
		stateMachine.pushState(new TaskNextState(this)).tick();
		loadTimer = System.currentTimeMillis();
		
		handleTab();
	}
	
	public void handleTab() {
		System.out.println("Opening tab");
		
		if (botTab != null) {
			botTab = openTab(curURL);
		} else
			botTab = openTab();
		
		botTab.getBrowserInstance().addLoadListener(loader);
		
		botTab.getTabView().addPaintListener(this);
		
		methods = new ScriptMethods(botTab);
		
		loadBlog();
	}
	
	public long getMainFrameID() {
		return mainFrameID;
	}
	
	private void openSignatureDialog() {
		setSigFrame.setVisible(true);
	}
	
	public Task getCurrentTask() {
		return currentTask;
	}
	
	public void loadBlog() {
		if (currentTask != null) {
			if (currentTask.url.endsWith("/"))
				botTab.loadURL(currentTask.url+"random");
			else
				botTab.loadURL(currentTask.url+"/random");
		}
	}
	
	public Tab getBotTab() {
		return botTab;
	}
	
	public ScriptMethods getScriptMethods() {
		return methods;
	}
	
	public ElementBounds getDebugElement() {
		return debugElement;
	}
	
	public void setDebugElement(ElementBounds debugElement) {
		this.debugElement = debugElement;
	}
	
	public DataEntry getHistoryConfig() {
		return historyConfig;
	}
	
	private void close() {
		botTab.getBrowserInstance().removeLoadListener(loader);
		botTab.getTabView().removePaintListener(this);
	}
	
	public String compileSignature(String urlString) {
		String base = Utils.getBaseURL(urlString);
		String title = base.split("\\.")[1];
		
		return signatureConfig.getValue().toString().replace("$title", title).replace("$base", base);
	}
	
	public LinkedList<Task> getTasks() {
		return tasks;
	}
	
	public void setCurrentTask(Task currentTask) {
		this.currentTask = currentTask;
	}
	
	@Override
	public int tick() {
		if (botTab != null) {
			/*if (mainFrameID > 0) {
				methods.getElements("$('.adsbygoogle').css('position', 'fixed').css('display', 'block').css('z-index', '99999999').css('left', '0px').css('top', '0px').show()");
			}*/
			if (skipTask) {
				System.out.println("Skipping task...");
				skipTask = false;
				stateMachine.clearStates().pushState(new TaskNextState(this));
			}
			
			boolean disposed = botTab.getBrowserInstance().isDisposed();
			if (disposed) {
				System.out.println("Apparently disposed. Opening new tab.");
				
				getTabs().closeTab(botTab);
				handleTab();
				
				return super.tick();
			}
			
			if (stateMachine.isFinished()) {
				return STATE_EXIT_SUCCESS;
			}
			
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					System.out.println("It's been two minutes. Browser must have crashed...");
					getTabs().closeTab(botTab);
					handleTab();
					loadTimer = System.currentTimeMillis();
				}
			}, 1*60*1000);

			try {
				stateMachine.tick();
			} catch (Exception e) {
				System.out.println("There was an error. Next task.");
				e.printStackTrace();
				stateMachine.clearStates().pushState(new TaskNextState(this)).tick();
			}
			
			timer.cancel();
			
			if (System.currentTimeMillis()-loadTimer >= MAX_WAIT_TIME*1000) {
				System.out.println("Been "+MAX_WAIT_TIME+" seconds. Forcing execution.");
				loadTimer = System.currentTimeMillis();
				stateMachine.pushEvent(Events.EVENT_PAGE_LOADED);
			}
		}
		
		return super.tick();
	}

	@Override
	public void onStart() {
		/*
		for (int i = 0; i < 5; i++)
			tasks.add(new Task("trippins.tumblr.com", 1, 0, 0, 1, ""));
			//*/
		
		new TaskManager(tasks);
		
		//tasks.add(new Task("http://sadiebrookes.com", 1, 0, 0, 1, ""));
		//executeTasks();
	}
	
	@Override
	public void onFinished() {
		close();
	}
	
	@Override
	public void onTerminate() {
		close();
	}

	@Override
	public void onPaint(Graphics g) {
		if (debugElement != null) {
			g.setColor(Color.green);
			g.drawRect(debugElement.x, debugElement.y, debugElement.width, debugElement.height);
		}
	}
	
	private JMenuItem executeTasks = new JMenuItem(new MenuAction("Execute Tasks", 1));
	
	@Override
	public void onJMenuCreated(JMenu menu) {
		JMenuItem setSignature = new JMenuItem(new MenuAction("Signature", 2));
		JMenuItem manageTasks = new JMenuItem(new MenuAction("Manage Tasks", 0));
		
		JMenuItem skipTask = new JMenuItem(new MenuAction("Skip Task", 3));
		JMenuItem stepExec = new JMenuItem(new MenuAction("Step execution", 4));
		JMenuItem openFb = new JMenuItem(new MenuAction("Open Facebook", 5));
		
		menu.add(setSignature);
		menu.add(manageTasks);
		menu.add(executeTasks);
		menu.addSeparator();
		menu.add(skipTask);
		menu.add(stepExec);
		menu.add(openFb);
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
				case 0: new TaskManager(tasks);
						break;
				case 1:	executeTasks();
						break;
				case 2:	openSignatureDialog();
						break;
				case 3:	skipTask = true;
						break;
				case 4:	stateMachine.pushEvent(Events.EVENT_PAGE_LOADED);
						break;
				case 5: openTab("www.facebook.com");
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
		
		//System.out.println(historyConfig.get("//history-entry[url/@value='http://global2.cmdolb.com/ops/akamai/images/r20.gif']").get(0).getValue("//clicks", 1));
		
		l = configEntries.get("//"+taskHistoryConfig.getKey());
		for (DataEntry history : l) {
			taskHistoryConfig = history;
		}
		
		l = configEntries.get("//"+signatureConfig.getKey());
		for (DataEntry signature : l) {
			signatureConfig = signature;
		}
		
		setSigFrame.setText(signatureConfig.getValue().toString());
		//System.out.println(historyConfig.contains("//*[@value='http://www.manageengine.com/products/applications_manager/applications-monitoring-features.html']"));
		//System.out.println(historyConfig.get("//*[@value='"+"http://www.manageengine.com/products/applications_manager/applications-monitoring-features.html".replace("https://", "http://")+"']"));
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
	public boolean shouldCreateMenu() {
		return true;
	}
}