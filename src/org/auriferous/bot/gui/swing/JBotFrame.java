package org.auriferous.bot.gui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.auriferous.bot.Bot;
import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.script.JScriptGui;
import org.auriferous.bot.gui.swing.script.selector.JScriptSelectorFrame;
import org.auriferous.bot.gui.swing.tabs.JTab;
import org.auriferous.bot.gui.swing.tabs.JTabBar;
import org.auriferous.bot.internal.executor.ScriptExecutionListener;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.scripts.internal.debug.TestElementSearch;
import org.auriferous.bot.shared.tabs.Tab;
import org.auriferous.bot.shared.tabs.Tabs;

public class JBotFrame extends JFrame implements ScriptExecutionListener, ChangeListener{
	
	static {
		/*try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	private static final int WIDTH = 1300;
	private static final int HEIGHT = 1000;
	
	private static final int ACTION_RUN_SCRIPT = 0;
	private static final int ACTION_ENABLE_DEBUG = 1;
	private static final int ACTION_EXIT_BOT = 2;
	private static final int ACTION_TERMINATE_SCRIPT = 3;
	private static final int ACTION_CREATE_TAB = 4;
	private static final int ACTION_PAUSE_SCRIPT = 5;
	private static final int ACTION_RESUME_SCRIPT = 6;
	private static final int ACTION_TAB_GO_BACK = 7;
	private static final int ACTION_TAB_GO_FORWARD = 8;
	private static final int ACTION_BLOCK_INPUT = 9;
	private static final int ACTION_TAB_REFRESH = 10;
	private static final int ACTION_DEBUG_ELEMENTS = 11;
	private static final int ACTION_CHECK_URL = 12;
	
	public static boolean mouseBlocked = false;
	public static boolean keyboardBlocked = false;
	
	private Bot bot;
	
	public JTabBar tabBar;
	
	private JDebugFrame debugger;
	
	private JMenu scriptsMenu;
	
	private Map<Script, JMenu> scriptMenuMap = new HashMap<Script, JMenu>();

	private JOverlayComponent paintableComponent = new JOverlayComponent();
	private Tabs userTabs;
	
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
		
		userTabs = bot.getUserTabs();
		
		tabBar = new JTabBar(paintableComponent, userTabs);
		tabBar.addChangeListener(this);
		add(tabBar);
		
		bot.getScriptExecutor().addScriptExecutionListener(this);
		
		debugger = new JDebugFrame(this);
		debugger.setVisible(false);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				debugger.cleanup();
			}
		});
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//setSize(WIDTH, HEIGHT);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setLocationRelativeTo(null);
		
		pack();
		setVisible(true);
		
		setGlassPane(paintableComponent);
		paintableComponent.setVisible(true);
	}
	
	public JOverlayComponent getPaintComponent() {
		return paintableComponent;
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
		tabsMenu.add(new MenuActionItem("Refresh", ACTION_TAB_REFRESH));
		tabsMenu.add(new MenuActionItem("Go back", ACTION_TAB_GO_BACK));
		tabsMenu.add(new MenuActionItem("Go forward", ACTION_TAB_GO_FORWARD));
		
		tabsMenu.addSeparator();
		tabsMenu.add(new MenuActionItem("Create", ACTION_CREATE_TAB));
		
		return tabsMenu;
	}
	
	private JMenu createDebugMenu() {
		JMenu debugMenu = new JMenu("Debug");
		
		debugMenu.add(new MenuActionItem("Check URL", ACTION_CHECK_URL));
		debugMenu.add(new MenuActionItem("Show", ACTION_ENABLE_DEBUG));
		debugMenu.add(new MenuActionItem("Elements", ACTION_DEBUG_ELEMENTS));
		debugMenu.add(new MenuActionItem("Block input", ACTION_BLOCK_INPUT));
		
		return debugMenu;
	}
	
	private JScriptSelectorFrame createScriptSelector(JMenu scriptsMenu) {
		return new JScriptSelectorFrame(this, bot);
	}

	@Override
	public void onRunScript(Script script) {
		tabBar.addTabs(script.getTabs());
		
		if (script instanceof JScriptGui)
			if (((JScriptGui)script).shouldCreateMenu())
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
		pauseItem.putValue(Action.NAME, "Resume");
	}
	
	@Override
	public void onResumeScript(Script script) {
		JMenu menu = scriptMenuMap.get(script);
		MenuActionItem pauseItem = (MenuActionItem) menu.getItem(menu.getItemCount()-2).getAction();
		
		pauseItem.setAction(ACTION_PAUSE_SCRIPT);
		pauseItem.putValue(Action.NAME, "Pause");
	}
	
	private void addScriptToMenu(Script script) {
		if (bot.getScriptExecutor().getNumberOfScripts() == 1) {
			scriptsMenu.addSeparator();
		}
		
		JMenu menu = new JMenu(script.getManifest().getName());
		if (script instanceof JScriptGui)
			((JScriptGui)script).onJMenuCreated(menu);
		
		if (menu.getItemCount() > 0)
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
			debugger.debug(((JTab)comp).getTab().getBrowserInstance());
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
			Tab current = tabBar.getCurrentTab();
			switch (this.actionID) {
			case ACTION_RUN_SCRIPT:
				createScriptSelector(scriptsMenu);
				break;
			case ACTION_ENABLE_DEBUG:
				debugger.setVisible(true);
				break;
			case ACTION_BLOCK_INPUT:
				JBotFrame.mouseBlocked = !JBotFrame.mouseBlocked;
				JBotFrame.keyboardBlocked = !JBotFrame.keyboardBlocked;
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
			case ACTION_TAB_GO_BACK:
				if (current != null) {
					current.goBack();
				} else {
					Utils.alert("Cannot access tab.");
				}
				break;
			case ACTION_TAB_GO_FORWARD:
				if (current != null) {
					current.goForward();
				} else {
					Utils.alert("Cannot access tab.");
				}
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
			case ACTION_TAB_REFRESH:
				if (current != null) {
					current.reload();
				} else {
					Utils.alert("Cannot access tab.");
				}
				break;
			case ACTION_DEBUG_ELEMENTS:
				try {
					TestElementSearch elementSearcher = (TestElementSearch) bot.getScriptLoader().loadScript("6");
					elementSearcher.setDebugTab(current);
					bot.getScriptExecutor().runScript(elementSearcher);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				break;
			case ACTION_CHECK_URL:
				System.out.println("URL: "+current.getURL());
				break;
			case ACTION_EXIT_BOT:
				System.exit(1);
			}
		}
	}
}
