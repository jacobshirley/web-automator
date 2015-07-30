package org.auriferous.bot.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.tabs.Tabs;
import org.auriferous.bot.tasks.ClickAdTask;
import org.auriferous.bot.tasks.TaskExecutor;

import com.teamdev.jxbrowser.chromium.LoggerProvider;

public class Bot implements ScriptSelectorListener {
	private static final int ACTION_ADD_TASK = 0;
	private static final int ACTION_REMOVE_TASK = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;

	private TaskExecutor tasks = new TaskExecutor();
	
	private JFrame frame;
	
	private Tabs tabs = null;
	
	public Bot() {
		LoggerProvider.setLevel(Level.OFF);
		
		//Create bot frame
		
        frame = new JFrame("Ad Clicker");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1300, 1000);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		//Menu bar
		
		JMenuBar menuBar = new JMenuBar();
		JMenu scriptMenu = new JMenu("Scripts");
		
		JMenuItem addTasksItem = new JMenuItem(new MenuAction("Add", ACTION_ADD_TASK));
		JMenuItem removeTasksItem = new JMenuItem(new MenuAction("Remove", ACTION_REMOVE_TASK));
		
		scriptMenu.add(addTasksItem);
		scriptMenu.add(removeTasksItem);

		menuBar.add(scriptMenu);
		
		frame.setJMenuBar(menuBar);
		
		//Tabs
	
		tabs = new Tabs(this);
		tabs.openTab("www.google.co.uk");
		tabs.openTab("www.bbc.co.uk");
		
		System.out.println(tabs.getCurrentTab().getBrowserWindow());
        
        //Tasks
        
        /*tasks.processTasks();
        
        context = new ScriptContext(this, tabs.getCurrentTab());
        
        OnAdTask task = new OnAdTask(context);
        
        tasks.addTask(task);*/
        
        //OnAdTask task = new OnAdTask(context);
        
        //tasks.addTask(task);
        
        //paintListeners.add(task);
        
        //new Humaniser(this).start();
	}

	ScriptContext context = null;//new ScriptContext(this);

	public Tabs getTabs() {
		return tabs;
	}
	
	public Frame getFrame() {
		return frame;
	}
	
	private ScriptSelector createScriptSelector() {
		return new ScriptSelector();
	}
	
	@Override
	public void onScriptSelected(String name) {
		ClickAdTask adTask = new ClickAdTask(new ScriptContext(this, tabs.getCurrentTab()));
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
			switch (this.actionID) {
			case ACTION_ADD_TASK:
				createScriptSelector().addScriptSelectorListener(Bot.this);
			}
		}
	}

	
}
