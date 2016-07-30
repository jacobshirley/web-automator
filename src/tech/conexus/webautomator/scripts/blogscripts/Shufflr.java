package tech.conexus.webautomator.scripts.blogscripts;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import tech.conexus.webautomator.Utils;
import tech.conexus.webautomator.gui.swing.script.JScriptGui;
import tech.conexus.webautomator.script.Script;
import tech.conexus.webautomator.script.ScriptContext;
import tech.conexus.webautomator.script.ScriptMethods;
import tech.conexus.webautomator.script.dom.ElementBounds;
import tech.conexus.webautomator.scripts.blogscripts.AdClicker.MenuAction;
import tech.conexus.webautomator.scripts.blogscripts.chrome.history.JHistoryFrame;
import tech.conexus.webautomator.scripts.blogscripts.events.Events;
import tech.conexus.webautomator.scripts.blogscripts.gui.JBlackListFrame;
import tech.conexus.webautomator.scripts.blogscripts.gui.JTaskManagerFrame;
import tech.conexus.webautomator.scripts.blogscripts.states.TaskNextState;
import tech.conexus.webautomator.scripts.blogscripts.task.Task;
import tech.conexus.webautomator.scripts.blogscripts.task.TaskConfigEntry;
import tech.conexus.webautomator.shared.data.DataEntry;
import tech.conexus.webautomator.shared.data.config.Configurable;
import tech.conexus.webautomator.shared.data.library.ScriptManifest;
import tech.conexus.webautomator.shared.fsm.FSM;
import tech.conexus.webautomator.shared.tabs.Tab;

public class Shufflr extends BlogScript implements JScriptGui {
	public Shufflr(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}

	@Override
	public void onStart() {
		/*
		for (int i = 0; i < 5; i++)
			tasks.add(new Task("trippins.tumblr.com", 1, 0, 0, 1, ""));
			//*/
		
		new JTaskManagerFrame(getTasks(), true, getPreviousTasks());
	}
	
	
	private JMenuItem executeTasks = new JMenuItem(new MenuAction("Execute Tasks", 1));
	
	@Override
	public void onJMenuCreated(JMenu menu) {
		JMenuItem manageTasks = new JMenuItem(new MenuAction("Manage Tasks", 0));
		JMenuItem skipTask = new JMenuItem(new MenuAction("Skip Task", 3));
		JMenuItem stepExec = new JMenuItem(new MenuAction("Step execution", 4));
		JMenuItem openFb = new JMenuItem(new MenuAction("Open Facebook", 5));
		JMenuItem history = new JMenuItem(new MenuAction("History", 6));
		
		menu.add(manageTasks);
		menu.add(executeTasks);
		menu.addSeparator();
		menu.add(skipTask);
		menu.add(stepExec);
		menu.add(openFb);
		menu.add(history);
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
				case 0: new JTaskManagerFrame(getTasks(), true, getPreviousTasks());
						break;
				case 1:	executeTasks();
						break;
				case 3:	skipTask();
						break;
				case 4:	getStateMachine().pushEvent(Events.EVENT_PAGE_LOADED);
						break;
				case 5: openTab("www.facebook.com");
						break;
				case 6: new JHistoryFrame(context.getHistory());
						break;
			}
		}
	}
	
	@Override
	public boolean shouldCreateMenu() {
		return true;
	}
}
