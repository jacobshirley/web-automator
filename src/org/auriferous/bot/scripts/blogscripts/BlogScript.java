package org.auriferous.bot.scripts.blogscripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.script.JScriptGui;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.scripts.blogscripts.events.Events;
import org.auriferous.bot.scripts.blogscripts.gui.JTaskManagerFrame;
import org.auriferous.bot.scripts.blogscripts.gui.adclicker.JSetSignatureFrame;
import org.auriferous.bot.scripts.blogscripts.states.TaskNextState;
import org.auriferous.bot.scripts.blogscripts.task.Task;
import org.auriferous.bot.scripts.blogscripts.task.TaskConfigEntry;
import org.auriferous.bot.shared.data.DataEntry;
import org.auriferous.bot.shared.data.config.Configurable;
import org.auriferous.bot.shared.data.history.HistoryEntry;
import org.auriferous.bot.shared.data.library.ScriptManifest;
import org.auriferous.bot.shared.fsm.FSM;
import org.auriferous.bot.shared.tabs.Tab;
import org.auriferous.bot.shared.tabs.view.PaintListener;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public abstract class BlogScript extends Script implements PaintListener, Configurable{
	private static final int MAX_WAIT_TIME = 10;
	
	private Tab botTab;
	private ScriptMethods methods;
	private boolean skipTask = false;
	
	private Task currentTask = null;
	
	private LinkedList<Task> tasks = new LinkedList<Task>();
	private List<Task> previousTasks = new LinkedList<Task>();
	
	private String blogURL;
	
	private ElementBounds debugElement = null;
	
	private DataEntry taskConfig = new DataEntry("tasks");
	private DataEntry taskHistoryConfig = new DataEntry("task-history");
	
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
	
	public BlogScript(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	public void executeTasks() {
		System.out.println(this.getClass().getName());
		stateMachine.pushState(new TaskNextState(this));
		loadTimer = System.currentTimeMillis();
	}
	
	public void skipTask() {
		skipTask = true;
	}
	
	public List<Task> getPreviousTasks() {
		return previousTasks;
	}
	
	public FSM getStateMachine() {
		return stateMachine;
	}
	
	public void handleTab() {
		System.out.println("Opening tab");
		
		if (botTab != null) {
			botTab = openTab(curURL);
		} else
			botTab = openTab(curURL);
		
		botTab.getBrowserInstance().addLoadListener(loader);
		
		botTab.getTabView().addPaintListener(this);
		
		methods = new ScriptMethods(botTab);
		
		loadBlog();
	}
	
	public void resetTimer() {
		loadTimer = System.currentTimeMillis();
	}
	
	public long getMainFrameID() {
		return mainFrameID;
	}
	
	public Task getCurrentTask() {
		return currentTask;
	}
	
	public void loadBlog() {
		if (currentTask != null) {
			String url = currentTask.url;
			if (!url.endsWith("random") && !url.endsWith("random/")) {
				if (currentTask.url.endsWith("/"))
					url += "random";
				else
					url += "/random";
			}
			botTab.loadURL(url);
		}
	}
	
	public boolean onBlog() {
		String url = botTab.getURL();
		return url.contains(Utils.getBaseURL(this.blogURL));
	}
	
	public void saveBlogURL() {
		this.blogURL = botTab.getURL();
	}
	
	public void setBotTab(Tab botTab) {
		this.botTab = botTab;
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
	
	private void close() {
		if (botTab != null) {
			botTab.getBrowserInstance().removeLoadListener(loader);
			botTab.getTabView().removePaintListener(this);
		}
	}
	
	public LinkedList<Task> getTasks() {
		return tasks;
	}
	
	public void setNextTask(Task currentTask) {
		if (currentTask != null) {
			this.currentTask = currentTask.copy();
			this.previousTasks.add(this.currentTask);
		}
	}
	
	@Override
	public int tick() {
		if (!stateMachine.isFinished()) {
			if (skipTask) {
				System.out.println("Skipping task...");
				skipTask = false;
				stateMachine.clearStates().pushState(new TaskNextState(this));
			}
			
			if (botTab != null) {
				boolean disposed = botTab.getBrowserInstance().isDisposed();
				if (disposed) {
					getTabs().closeTab(botTab);
					System.out.println("Apparently disposed. Opening new tab.");
					handleTab();
					
					return super.tick();
				}
			}

			try {
				stateMachine.tick();
			} catch (Exception e) {
				System.out.println("There was an error. Next task.");
				e.printStackTrace();
				stateMachine.clearStates().pushState(new TaskNextState(this)).tick();
			}
			
			if (System.currentTimeMillis()-loadTimer >= MAX_WAIT_TIME*1000) {
				System.out.println("Been "+MAX_WAIT_TIME+" seconds. Forcing execution.");
				loadTimer = System.currentTimeMillis();
				stateMachine.pushEvent(Events.EVENT_PAGE_LOADED);
			}
		} else if (botTab != null) {
			status = STATE_EXIT_SUCCESS;
		}
		
		return super.tick();
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

		l = configEntries.get("//"+taskHistoryConfig.getKey());
		for (DataEntry history : l) {
			taskHistoryConfig = history;
			
			for (DataEntry taskEntry : taskHistoryConfig.getChildren()) {
				Task t = new Task(taskEntry);
				
				this.previousTasks.add(t);
			}
		}
		
		
	}

	@Override
	public void save(DataEntry root) {
		taskConfig.clear();
		
		for (Task t : tasks) {
			taskConfig.add(new TaskConfigEntry(t));
		}

		root.add(taskConfig, true);

		taskHistoryConfig.clear();
		for (Task t : previousTasks) {
			taskHistoryConfig.add(new TaskConfigEntry(t));
		}
		
		root.add(taskHistoryConfig, true);
	}
}