package org.auriferous.bot.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.auriferous.bot.Bot;
import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.scriptselector.ScriptSelector;
import org.auriferous.bot.gui.scriptselector.ScriptSelectorListener;
import org.auriferous.bot.gui.tabs.TabBar;
import org.auriferous.bot.gui.tabs.TabView;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.loader.ScriptLoader;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.executor.ScriptExecutor;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.scripts.OnAdTask;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabPaintListener;
import org.auriferous.bot.tabs.Tabs;

public class BotGUI extends JFrame implements ScriptSelectorListener{
	private static final int ACTION_ADD_TASK = 0;
	private static final int ACTION_REMOVE_TASK = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;
	
	private static final int REFRESH_RATE = 50;
	private static final int UPDATE_INTERVAL = 1000/REFRESH_RATE;
	
	private Bot bot;
	
	private TabBar tabBar;
	
	public BotGUI(Bot bot) {
		super("Ad Clicker");
		this.bot = bot;
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1300, 1000);
		
		setLocationRelativeTo(null);
		setVisible(true);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu scriptMenu = new JMenu("Scripts");
		
		JMenuItem addTasksItem = new JMenuItem(new MenuAction("Add", ACTION_ADD_TASK));
		JMenuItem removeTasksItem = new JMenuItem(new MenuAction("Remove", ACTION_REMOVE_TASK));
		
		scriptMenu.add(addTasksItem);
		scriptMenu.add(removeTasksItem);

		menuBar.add(scriptMenu);
		
		setJMenuBar(menuBar);
		
		final Tabs tabs = new Tabs();
		
		tabBar = new TabBar(tabs);
		add(tabBar);
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					synchronized (tabs) {
						if (tabs.hasTabs()) {
							int index = tabBar.getSelectedIndex();
							if (index >= 0) {
								TabView view = (TabView) tabBar.getSelectedComponent();
								
								if (System.currentTimeMillis() - view.getLastTimePainted() >= UPDATE_INTERVAL) {
									view.repaint(UPDATE_INTERVAL);
								}
								Utils.wait(UPDATE_INTERVAL);
							}
						}
					}
					Thread.yield();
				}
			}
		}).start();
		
		tabs.openTab("www.google.co.uk");
	}
	
	private ScriptSelector createScriptSelector() {
		return new ScriptSelector(this, bot);
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
				createScriptSelector().addScriptSelectorListener(BotGUI.this);
			}
		}
	}

	@Override
	public void onScriptSelected(Script script) {
		tabBar.addTabs(script.getTabs());
	}
}
