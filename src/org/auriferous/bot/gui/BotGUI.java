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
import org.auriferous.bot.gui.tabs.TabBar;
import org.auriferous.bot.gui.tabs.TabView;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptExecutor;
import org.auriferous.bot.script.ScriptContext;
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
		
		JTabbedPane tC = new TabBar(bot.getTabs());
		add(tC);
		
		final Tabs tabs = bot.getTabs();
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (tabs.hasTabs()) {
						Tab cur = tabs.getCurrentTab();
						TabView view = cur.getTabView();
						
						if (System.currentTimeMillis() - view.getLastTimePainted() >= UPDATE_INTERVAL) {
							view.repaint();
						}
						Utils.wait(UPDATE_INTERVAL);
					}
					Thread.yield();
				}
			}
		}).start();
		
		bot.getTabs().openTab("www.google.co.uk");
	}
	
	private ScriptSelector createScriptSelector() {
		return new ScriptSelector(this);
	}
	
	@Override
	public void onScriptSelected(String name) {
		//bot.getTabs().openTab("www.youtube.com");
		//System.out.println(bot.getTabs().getCurrentTab().getID());
		Tab currentTab = bot.getTabs().getCurrentTab();
		
		ScriptContext context = new ScriptContext(bot, currentTab);
		
		Script linkClicker = new OnAdTask(context);
		ScriptExecutor bundle = new ScriptExecutor(context, new Script[] {linkClicker});
		
		if (linkClicker instanceof TabPaintListener) {
			System.out.println("Instance");
			currentTab.getTabView().addTabPaintListener((TabPaintListener)linkClicker);
		}
		
		bundle.processScripts();//*/
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
}
