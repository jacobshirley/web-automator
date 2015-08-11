package org.auriferous.bot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
import org.auriferous.bot.script.executor.ScriptExecutionListener;
import org.auriferous.bot.script.executor.ScriptExecutor;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.scripts.OnAdTask;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabPaintListener;
import org.auriferous.bot.tabs.Tabs;

public class BotGUI extends JFrame implements ScriptSelectorListener, ScriptExecutionListener{
	
	static {
		/*try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	private static final int ACTION_RUN_SCRIPT = 0;
	private static final int ACTION_ENABLE_DEBUG = 1;
	private static final int ACTION_EXIT_BOT = 2;
	private static final int ACTION_TERMINATE_SCRIPT = 3;
	
	private static final int REFRESH_RATE = 50;
	private static final int UPDATE_INTERVAL = 1000/REFRESH_RATE;
	
	private Bot bot;
	
	public TabBar tabBar;
	private Tabs userTabs;
	
	private JMenu scriptsMenu;
	
	private Map<Script, JMenu> scriptMenuMap = new HashMap<Script, JMenu>();
	
	public BotGUI(final Bot bot) {
		super("Web Automator");
		
		this.bot = bot;
	
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1300, 1000);
		
		setLocationRelativeTo(null);
		setVisible(true);
		
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(createFileMenu());
		
		scriptsMenu = createScriptsMenu();
		
		menuBar.add(scriptsMenu);
		menuBar.add(createDebugMenu());
		
		setJMenuBar(menuBar);
		
		userTabs = new Tabs();
		
		tabBar = new TabBar(userTabs);
		add(tabBar);
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					//synchronized (tabBar) {
						int index = tabBar.getSelectedIndex();
						if (index >= 0) {
							TabView view = (TabView) tabBar.getSelectedComponent();
							
							if (System.currentTimeMillis() - view.getLastTimePainted() >= UPDATE_INTERVAL) {
								view.repaint(UPDATE_INTERVAL);
							}
							Utils.wait(UPDATE_INTERVAL);
						}
					//}
					Thread.yield();
				}
			}
		}).start();
		
		bot.getScriptExecutor().addScriptExecutionListener(this);
	}
	
	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem exitBotItem = new JMenuItem(new MenuAction("Exit", ACTION_EXIT_BOT));
		
		fileMenu.add(exitBotItem);
		
		return fileMenu;
	}
	
	private JMenu createScriptsMenu() {
		JMenu scriptsMenu = new JMenu("Scripts");
		
		JMenuItem runScriptItem = new JMenuItem(new MenuAction("Run", ACTION_RUN_SCRIPT));
		
		scriptsMenu.add(runScriptItem);
		return scriptsMenu;
	}
	
	private JMenu createDebugMenu() {
		JMenu debugMenu = new JMenu("Debug");
		return debugMenu;
	}
	
	private ScriptSelector createScriptSelector(JMenu scriptsMenu) {
		return new ScriptSelector(this, scriptsMenu, bot);
	}

	@Override
	public void onScriptSelected(Script script) {
		tabBar.addTabs(script.getTabs());
		addScriptToMenu(script);
	}

	@Override
	public void onRunScript(Script script) {
	}

	@Override
	public void onScriptFinished(Script script) {
		//tabBar.removeTabs(script.getTabs());
		removeScriptFromMenu(script);
	}

	@Override
	public void onTerminateScript(Script script) {
		tabBar.removeTabs(script.getTabs());
		removeScriptFromMenu(script);
	}

	@Override
	public void onPauseScript(Script script) {
	}
	
	private void addScriptToMenu(Script script) {
		if (bot.getScriptExecutor().getNumberOfScripts() == 1) {
			scriptsMenu.addSeparator();
		}
		
		JMenu menu = new JMenu(script.getManifest().getName());
		script.onGUICreated(menu);
		
		menu.addSeparator();
		menu.add(new MenuAction("Terminate", script, ACTION_TERMINATE_SCRIPT));
		
		scriptMenuMap.put(script, menu);
		scriptsMenu.add(menu);
	}
	
	private void removeScriptFromMenu(Script script) {
		scriptsMenu.remove(scriptMenuMap.get(script));
		scriptMenuMap.remove(script);
		
		scriptsMenu.revalidate();
	}
	
	class MenuAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private int actionID;

		private Script script = null;
		
		public MenuAction(String text, int actionID) {
			super(text);
			this.actionID = actionID;
		}
		
		public MenuAction(String text, Script script, int actionID) {
			super(text);
			this.script = script;
			this.actionID = actionID;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (this.actionID) {
			case ACTION_RUN_SCRIPT:
				createScriptSelector(scriptsMenu).addScriptSelectorListener(BotGUI.this);
				break;
			case ACTION_TERMINATE_SCRIPT:
				bot.getScriptExecutor().terminateScript(script);
				break;
			case ACTION_EXIT_BOT:
				System.exit(1);
			}
		}
	}
}
