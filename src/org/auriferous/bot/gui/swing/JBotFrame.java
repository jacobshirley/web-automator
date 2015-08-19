package org.auriferous.bot.gui.swing;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.auriferous.bot.Bot;
import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.script.JScriptGuiListener;
import org.auriferous.bot.gui.swing.script.selector.JScriptSelectorFrame;
import org.auriferous.bot.gui.swing.tabs.JTab;
import org.auriferous.bot.gui.swing.tabs.JTabBar;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.executor.ScriptExecutionListener;
import org.auriferous.bot.tabs.Tabs;
import org.auriferous.bot.tabs.view.TabView;

public class JBotFrame extends JFrame implements ScriptExecutionListener, ChangeListener{
	
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
	private static final int ACTION_CREATE_TAB = 4;
	private static final int ACTION_PAUSE_SCRIPT = 5;
	private static final int ACTION_RESUME_SCRIPT = 6;
	
	private static final int REFRESH_RATE = 20;
	private static final int UPDATE_INTERVAL = 1000/REFRESH_RATE;
	
	private Bot bot;
	
	public JTabBar tabBar;
	private Tabs userTabs;
	
	private JDebugFrame debugger;
	
	private JMenu scriptsMenu;
	
	private Map<Script, JMenu> scriptMenuMap = new HashMap<Script, JMenu>();
	
	private Window window = new Window(this);
	
	public JBotFrame(final Bot bot) {
		super("Web Automator");
		
		this.bot = bot;
		
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(createFileMenu());
		
		scriptsMenu = createScriptsMenu();
		
		menuBar.add(scriptsMenu);
		menuBar.add(createTabsMenu());
		menuBar.add(createDebugMenu());
		
		setJMenuBar(menuBar);
		
		userTabs = new Tabs();
		
		tabBar = new JTabBar(userTabs);
		tabBar.addChangeListener(this);
		add(tabBar);
		
		bot.getScriptExecutor().addScriptExecutionListener(this);
		
		debugger = new JDebugFrame(this);
		debugger.setVisible(false);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					int index = tabBar.getSelectedIndex();
					if (index >= 0) {
						TabView view = (TabView) ((JTab)tabBar.getSelectedComponent()).getTabView();
						
						if (System.currentTimeMillis() - view.getLastTimePainted() >= UPDATE_INTERVAL) {
							view.repaint(UPDATE_INTERVAL);
						}
						
						Thread.yield();
					}
				}
			}
		}).start();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				debugger.cleanup();
			}
		});
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1300, 1000);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
	}
	
	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem exitBotItem = new JMenuItem(new MenuActionItem("Exit", ACTION_EXIT_BOT));
		
		fileMenu.add(exitBotItem);
		
		return fileMenu;
	}
	
	private JMenu createScriptsMenu() {
		JMenu scriptsMenu = new JMenu("Scripts");
		
		JMenuItem runScriptItem = new JMenuItem(new MenuActionItem("Run", ACTION_RUN_SCRIPT));
		
		scriptsMenu.add(runScriptItem);
		return scriptsMenu;
	}
	
	private JMenu createTabsMenu() {
		JMenu tabsMenu = new JMenu("Tabs");
		
		tabsMenu.add(new MenuActionItem("Create", ACTION_CREATE_TAB));
		
		return tabsMenu;
	}
	
	private JMenu createDebugMenu() {
		JMenu debugMenu = new JMenu("Debug");
		
		debugMenu.add(new MenuActionItem("Show", ACTION_ENABLE_DEBUG));
		
		return debugMenu;
	}
	
	private JScriptSelectorFrame createScriptSelector(JMenu scriptsMenu) {
		return new JScriptSelectorFrame(this, bot);
	}

	@Override
	public void onRunScript(Script script) {
		tabBar.addTabs(script.getTabs());
		addScriptToMenu(script);
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
		JMenu menu = scriptMenuMap.get(script);
		MenuActionItem pauseItem = (MenuActionItem) menu.getItem(menu.getItemCount()-2).getAction();
		
		pauseItem.setAction(ACTION_RESUME_SCRIPT);
		pauseItem.putValue(MenuActionItem.NAME, "Resume");
	}
	
	@Override
	public void onResumeScript(Script script) {
		JMenu menu = scriptMenuMap.get(script);
		MenuActionItem pauseItem = (MenuActionItem) menu.getItem(menu.getItemCount()-2).getAction();
		
		pauseItem.setAction(ACTION_PAUSE_SCRIPT);
		pauseItem.putValue(MenuActionItem.NAME, "Pause");
	}
	
	private void addScriptToMenu(Script script) {
		if (bot.getScriptExecutor().getNumberOfScripts() == 1) {
			scriptsMenu.addSeparator();
		}
		
		JMenu menu = new JMenu(script.getManifest().getName());
		if (script instanceof JScriptGuiListener)
			((JScriptGuiListener)script).onJMenuCreated(menu);
		
		menu.addSeparator();
		menu.add(new MenuActionItem("Pause", script, ACTION_PAUSE_SCRIPT));
		menu.add(new MenuActionItem("Terminate", script, ACTION_TERMINATE_SCRIPT));
		
		scriptMenuMap.put(script, menu);
		scriptsMenu.add(menu);
	}
	
	private void removeScriptFromMenu(Script script) {
		scriptsMenu.remove(scriptMenuMap.get(script));
		scriptMenuMap.remove(script);
		
		if (scriptMenuMap.isEmpty()) {
			JMenuItem item = scriptsMenu.getItem(0);
			
			scriptsMenu.removeAll();
			scriptsMenu.add(item);
		}
		
		scriptsMenu.revalidate();
	}
	
	@Override
	public void stateChanged(ChangeEvent cE) {
		Component comp = tabBar.getSelectedComponent();
		if (comp != null)
			debugger.debug(((JTab)comp).getTab().getBrowserWindow());
	}
	
	class MenuActionItem extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private int actionID;

		private Script script = null;
		
		public MenuActionItem(String text, int actionID) {
			super(text);
			this.actionID = actionID;
		}
		
		public MenuActionItem(String text, Script script, int actionID) {
			super(text);
			this.script = script;
			this.actionID = actionID;
		}
		
		public void setAction(int id) {
			this.actionID = id;
		}
		
		public int getAction() {
			return actionID;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (this.actionID) {
			case ACTION_RUN_SCRIPT:
				createScriptSelector(scriptsMenu);
				break;
			case ACTION_ENABLE_DEBUG:
				debugger.setVisible(true);
				break;
			case ACTION_TERMINATE_SCRIPT:
				bot.getScriptExecutor().terminateScript(script);
				break;
			case ACTION_PAUSE_SCRIPT:
				bot.getScriptExecutor().pauseScript(script);
				break;
			case ACTION_RESUME_SCRIPT:
				bot.getScriptExecutor().resumeScript(script);
				break;
			case ACTION_CREATE_TAB:
				String s = (String)JOptionPane.showInputDialog(
	                    JBotFrame.this,
	                    "Tab URL: ",
	                    "Enter a URL",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    "http://www.google.com/");
				
				if ((s != null) && (s.length() > 0)) {
					userTabs.openTab(s);
				}
				break;
			case ACTION_EXIT_BOT:
				System.exit(1);
			}
		}
	}
}
