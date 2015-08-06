package org.auriferous.bot.scripts.adclicker;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.BotGUI;
import org.auriferous.bot.script.ElementRect;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.scripts.adclicker.gui.TaskManager;
import org.auriferous.bot.tabs.Tab;

import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

public class AdClicker extends Script implements LoadListener, Runnable{
	private static final int STAGE_SHUFFLES = 0;
	private static final int STAGE_URL = 1;
	private static final int STAGE_WAIT_ON_AD = 2;
	private static final int STAGE_DONE = 3;
	
	private int curAdClick = 0;
	private int curShuffles = 0;
	private int curSubClick = 0;

	private int taskStage = STAGE_URL;
	
	private Task currentTask = null;
	private List<Task> tasks = new ArrayList<Task>();
	
	private JMenu adMenu;
	
	public AdClicker(ScriptContext context) {
		super(context);
		
		JMenuBar menuBar = context.getBotGUI().getJMenuBar();
		
		adMenu = new JMenu("Ad Clicker");
		
		JMenuItem manageTasks = new JMenuItem(new MenuAction("Manage Tasks", 0));
		JMenuItem executeTasks = new JMenuItem(new MenuAction("Execute Tasks", 1));
		
		adMenu.add(manageTasks);
		adMenu.add(executeTasks);
		
		menuBar.add(adMenu);
		menuBar.revalidate();
	}
	
	@Override
	public void run() {
		System.out.println("opening tab");
		botTab = openTab();
		methods = new ScriptMethods(botTab);
		botTab.getBrowserWindow().addLoadListener(this);
		
		for (Task task : tasks) {
			System.out.println("Starting task on "+task.url);
			botTab.loadURL(task.url);
			
			currentTask = task;
			
			while (taskStage != STAGE_DONE) {
				Thread.yield();
			}
		}
		
		status = STATE_EXIT_SUCCESS;
	}
	
	private Tab botTab;
	private ScriptMethods methods;
	
	private void executeTasks() {
		new Thread(this).start();
	}
	
	@Override
	public int tick() {
		
		return super.tick();
	}

	@Override
	public void onStart() {
		System.out.println("Running...");
	}

	@Override
	public void onPause() {
		
	}

	@Override
	public void onTerminate() {
		
	}
	
	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent event) {
		long frameID = event.getFrameId();
		
		ElementRect adElement = null;
		
		if (taskStage == STAGE_SHUFFLES) {
			if (event.isMainFrame()) {
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
		} 
		if (taskStage == STAGE_URL) {
	        if (event.isMainFrame()) {
	        	System.out.println("starting ad clicking");
	        	
	        	adElement = methods.getRandomElement("$('.rh-title');");
	        	
	        	if (adElement == null)
	        		methods.getRandomElement("$('.adsbygoogle').first();");
	        	
	        	Point p = adElement.getRandomPointInRect();
	        	
	        	System.out.println("Clicking at "+p.x+", "+p.y);
	        	
	        	methods.mouse(p.x, p.y);
	        	
	        	taskStage++;
	        	curAdClick++;
	        }
		} 
		if (taskStage == STAGE_WAIT_ON_AD) {
			System.out.println("Now waiting on ad");
			
			Utils.wait((int) currentTask.timeOnAd);
			taskStage++;
		}
	}

	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent event) {
	}

	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent event) {
	}

	@Override
	public void onFailLoadingFrame(FailLoadingEvent event) {
	}

	@Override
	public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
	}

	@Override
	public void onStartLoadingFrame(StartLoadingEvent event) {
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
				case 0: new TaskManager(context.getBotGUI(), tasks);
						break;
				case 1:	executeTasks();
						break;
			}
			
		}
	}
}
