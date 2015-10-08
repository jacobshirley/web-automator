package org.auriferous.bot.scripts.blogscripts;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.script.JScriptGui;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.scripts.blogscripts.chrome.history.JHistoryFrame;
import org.auriferous.bot.scripts.blogscripts.events.Events;
import org.auriferous.bot.scripts.blogscripts.gui.JBlackListFrame;
import org.auriferous.bot.scripts.blogscripts.gui.JTaskManagerFrame;
import org.auriferous.bot.scripts.blogscripts.gui.adclicker.JSetSignatureFrame;
import org.auriferous.bot.shared.data.DataEntry;
import org.auriferous.bot.shared.data.config.Configurable;
import org.auriferous.bot.shared.data.library.ScriptManifest;
import org.auriferous.bot.shared.tabs.view.PaintListener;

public class AdClicker extends BlogScript implements PaintListener, JScriptGui, Configurable{
	private JSetSignatureFrame setSigFrame = new JSetSignatureFrame(this);
	
	private DataEntry historyConfig = new DataEntry("click-history");
	public DataEntry signatureConfig = new DataEntry("signature", "");
	private DataEntry blacklistConfig = new DataEntry("blacklist");
	
	private List<String> blacklist = new ArrayList<String>();
	
	public AdClicker(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	@Override
	public void onStart() {
		/*
		for (int i = 0; i < 5; i++)
			tasks.add(new Task("trippins.tumblr.com", 1, 0, 0, 1, ""));
			//*/
		
		//new JTaskManagerFrame(getTasks(), false, getPreviousTasks());
		//new JBlackListFrame(getBlacklist());
		new JHistoryFrame(context.getHistory());
	}
	
	public List<String> getBlacklist() {
		return blacklist;
	}
	
	public DataEntry getHistoryConfig() {
		return historyConfig;
	}
	
	private void openSignatureDialog() {
		setSigFrame.setVisible(true);
	}

	public String compileSignature(String urlString) {
		String signatures = (String) signatureConfig.getValue();
		
		String[] sigs = signatures.split("\n\\s");
		if (sigs.length > 0) {
			return getSignature(Utils.getRandomObject(sigs), urlString);
		} else {
			return getSignature(signatures, urlString);
		}
	}
	
	private String getSignature(String signatureString, String url) {
		String base = Utils.getBaseURL(url);
		String title = base.split("\\.")[1];
		
		return signatureString.replace("$title", title).replace("$base", base);
	}
	
	@Override
	public void load(DataEntry configEntries) {
		super.load(configEntries);
		
		List<DataEntry> l = configEntries.get("//"+historyConfig.getKey());
		for (DataEntry history : l) {
			historyConfig = history;
		}
		
		l = configEntries.get("//"+signatureConfig.getKey());
		for (DataEntry signature : l) {
			signatureConfig = signature;
		}
		
		l = configEntries.get("//"+blacklistConfig.getKey());
		for (DataEntry blacklist : l) {
			blacklistConfig = blacklist;
			for (DataEntry entry : blacklistConfig.getChildren()) {
				this.blacklist.add(""+entry.getValue());
			}
		}
		
		setSigFrame.setText(signatureConfig.getValue().toString());
	}
	
	@Override
	public void save(DataEntry root) {
		super.save(root);
		
		root.add(historyConfig, true);
		root.add(signatureConfig, true);

		blacklistConfig.clear();
		for (String s : blacklist) {
			blacklistConfig.add(new DataEntry("entry", s));
		}
		
		root.add(blacklistConfig, true);
	}

	private JMenuItem executeTasks = new JMenuItem(new MenuAction("Execute Tasks", 1));
	
	@Override
	public void onJMenuCreated(JMenu menu) {
		JMenuItem manageTasks = new JMenuItem(new MenuAction("Manage Tasks", 0));
		JMenuItem setSignature = new JMenuItem(new MenuAction("Set Signature", 2));
		JMenuItem skipTask = new JMenuItem(new MenuAction("Skip Task", 3));
		JMenuItem stepExec = new JMenuItem(new MenuAction("Step execution", 4));
		JMenuItem openFb = new JMenuItem(new MenuAction("Open Facebook", 5));
		JMenuItem history = new JMenuItem(new MenuAction("History", 6));
		JMenuItem openBlacklist = new JMenuItem(new MenuAction("Blacklist", 7));
		
		menu.add(manageTasks);
		menu.add(setSignature);
		menu.add(executeTasks);
		menu.addSeparator();
		menu.add(skipTask);
		menu.add(stepExec);
		menu.add(openFb);
		menu.add(history);
		menu.add(openBlacklist);
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
				case 0: new JTaskManagerFrame(getTasks(), false, getPreviousTasks());
						break;
				case 1:	executeTasks();
						break;
				case 2: openSignatureDialog();
						break;
				case 3:	skipTask();
						break;
				case 4:	getStateMachine().pushEvent(Events.EVENT_PAGE_LOADED);
						break;
				case 5: openTab("www.facebook.com");
						break;
				case 6: new JHistoryFrame(context.getHistory());
						break;
				case 7: new JBlackListFrame(getBlacklist());
						break;
			}
		}
	}

	@Override
	public boolean shouldCreateMenu() {
		return true;
	}
}
