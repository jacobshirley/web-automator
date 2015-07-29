package org.adclicker.bot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.adclicker.bot.tasks.BotTask;
import org.adclicker.bot.tasks.ClickAdTask;
import org.adclicker.bot.tasks.OnAdTask;
import org.adclicker.input.KeyboardSimulator;
import org.adclicker.input.MouseSimulator;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.CertificateErrorParams;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.LoadHandler;
import com.teamdev.jxbrowser.chromium.LoadParams;
import com.teamdev.jxbrowser.chromium.LoggerProvider;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class Bot {
	private static final int ACTION_ADD_TASK = 0;
	private static final int ACTION_REMOVE_TASK = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;

	private Browser browser = new Browser();
	
	private MouseSimulator mouseSimulation;
	private KeyboardSimulator keyboardSimulation;

	private BrowserView browserView;
	private TaskExecutor tasks = new TaskExecutor();
	
	private JFrame frame;
	
	private List<PaintListener> paintListeners = new LinkedList<PaintListener>();
	
	public Bot() {
		LoggerProvider.setLevel(Level.OFF);
		
		this.browserView = new BrowserView(browser) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				for (PaintListener listener : paintListeners)
					listener.onPaint(g);
			}
		};

        frame = new JFrame("Ad Clicker");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(this.browserView, BorderLayout.CENTER);
		frame.setSize(1300, 1000);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
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
        
        this.mouseSimulation = new MouseSimulator(browserView, frame.getWidth(), frame.getHeight());
        
        paintListeners.add(this.mouseSimulation);
        
        this.browser.loadURL("www.google.co.uk");
        
        tasks.processTasks();
        
        tasks.addTask(new OnAdTask(context));
        
        //new Humaniser(this).start();
	}
	
	ScriptContext context = new ScriptContext(this);

	public Browser getBrowser() {
		return browser;
	}
	
	public MouseSimulator getMouseSimulator() {
		return mouseSimulation;
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
