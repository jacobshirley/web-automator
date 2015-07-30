package org.auriferous.bot.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.tabs.Tabs;
import org.auriferous.bot.tasks.ClickAdTask;
import org.auriferous.bot.tasks.TaskExecutor;

public class Bot {
	private static final int ACTION_ADD_TASK = 0;
	private static final int ACTION_REMOVE_TASK = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;

	private TaskExecutor tasks = new TaskExecutor();
	
	private JFrame frame;
	
	private Tabs tabs = null;
	
	public Bot() {
		/*LoggerProvider.setLevel(Level.OFF);
		
		//Create bot frame
		
        frame = new JFrame("Ad Clicker");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1300, 1000);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		//Menu bar
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		JMenu tasksItem = new JMenu("Tasks");
		
		JMenuItem addTasksItem = new JMenuItem(new MenuAction("Add", ACTION_ADD_TASK));
		JMenuItem removeTasksItem = new JMenuItem(new MenuAction("Remove", ACTION_REMOVE_TASK));
		
		tasksItem.add(addTasksItem);
		tasksItem.add(removeTasksItem);
		
		fileMenu.add(tasksItem);
		
		menuBar.add(fileMenu);
		
		frame.setJMenuBar(menuBar);
		
		//Tabs
	
		tabs = new Tabs(this);
		tabs.openTab("www.google.co.uk");
		//tabs.openTab("www.runescape.com");
		//tabs.openTab("www.bbc.co.uk");
		
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
		
		createScriptSelector();
	}

	ScriptContext context = null;//new ScriptContext(this);

	public Tabs getTabs() {
		return tabs;
	}
	
	public Frame getFrame() {
		return frame;
	}
	
	private JFrame createScriptSelector() {
		return new ScriptSelector();
	}
	
	private void createAddTaskDialog() {
		JTextField url = new JTextField();
		JTextField timeOnAd = new JTextField();
		JTextField shuffles = new JTextField();
		JTextField shuffleInterval = new JTextField();
		JTextField adClicks = new JTextField();
		JTextField adShuffles = new JTextField();
		JTextField subClicks = new JTextField();
		
		Object[] message = {
		    "URL:", url,
		    "Time on ad:", timeOnAd,
		    "Shuffles:", shuffles,
		    "Shuffle Interval:", shuffleInterval,
		    "Ad Clicks:", adClicks,
		    "Sub Clicks:", subClicks,
		};		

		int option = JOptionPane.showConfirmDialog(null, message, "Add Task", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			ClickAdTask task = new ClickAdTask(context, url.getText(), 
					Double.parseDouble(timeOnAd.getText()), 
					Integer.parseInt(adClicks.getText()), 
					Integer.parseInt(shuffles.getText()),
					Integer.parseInt(shuffleInterval.getText()),
					Integer.parseInt(subClicks.getText()));
			
			tasks.addTask(task);
		} else {
		    System.out.println("Cancelled");
		}
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
				createAddTaskDialog();
			}
		}
	}
}
