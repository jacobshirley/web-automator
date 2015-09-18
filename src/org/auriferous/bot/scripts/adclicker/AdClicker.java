package org.auriferous.bot.scripts.adclicker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
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

public class AdClicker extends Script implements PaintListener, JScriptGuiListener, Configurable{
	private static final int MAX_WAIT_TIME = 10;
	
	private String curURL;
	
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
	
	public AdClicker(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	private void executeTasks() {
		stateMachine.pushState(new TaskNextState(this)).tick();
		
		handleTab();
		loadTimer = System.currentTimeMillis();
	}
	
	public void handleTab() {
		System.out.println("Opening tab");
		
		if (botTab != null) {
			System.out.println("Loading URL "+curURL);
			botTab = openTab();
		} else
			botTab = openTab();
		
		botTab.getBrowserInstance().addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				if (event.isMainFrame()) {
					System.out.println("page loaded");
					loadTimer = System.currentTimeMillis();
					stateMachine.pushEvent(Events.EVENT_PAGE_LOADED);
				}
			}
		});
		
		botTab.getTabView().addPaintListener(this);
		
		methods = new ScriptMethods(botTab);
		
		loadBlog();
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
			if (skipTask) {
				System.out.println("Skipping task...");
				skipTask = false;
				stateMachine.clearStates().pushState(new TaskNextState(this));
			}
			
			boolean disposed = botTab.getBrowserInstance().isDisposed();
			if (disposed) {
				getTabs().closeTab(botTab);
				System.out.println("Apparently disposed. Opening new tab.");
				handleTab();
				
				return super.tick();
			}
			
			if (stateMachine.isFinished()) {
				return STATE_EXIT_SUCCESS;
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
		}
		
		return super.tick();
	}

	@Override
	public void onStart() {
		new TaskManager(tasks, taskConfig);
		//tasks.add(new Task("florats.tk", 1, 0, 0, 1, ""));
		//executeTasks();
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
		JMenuItem stepExec = new JMenuItem(new MenuAction("Step execution", 4));
		
		menu.add(setSignature);
		menu.add(manageTasks);
		menu.add(executeTasks);
		menu.addSeparator();
		menu.add(skipTask);
		menu.add(stepExec);
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
				case 2:	openSignatureDialog();
						break;
				case 3:	skipTask = true;
						break;
				case 4:	stateMachine.pushEvent(Events.EVENT_PAGE_LOADED);
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
}