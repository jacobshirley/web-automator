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
import org.auriferous.bot.ScriptBundle;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.scripts.OnAdTask;
import org.auriferous.bot.tabs.Tabs;
import org.auriferous.bot.tabs.gui.TabBar;

public class BotGUI extends JFrame implements ScriptSelectorListener{
	private static final int ACTION_ADD_TASK = 0;
	private static final int ACTION_REMOVE_TASK = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;
	
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
		
		bot.getTabs().openTab("www.google.co.uk");
	}
	
	private ScriptSelector createScriptSelector() {
		return new ScriptSelector(this);
	}
	
	@Override
	public void onScriptSelected(String name) {
		bot.getTabs().openTab("www.youtube.com");
		//System.out.println(bot.getTabs().getCurrentTab().getID());
		
		ScriptContext context = new ScriptContext(bot, bot.getTabs().getCurrentTab());
		
		Script linkClicker = new OnAdTask(context);
		ScriptBundle bundle = new ScriptBundle(context, new Script[] {linkClicker});
		
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
