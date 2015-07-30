package org.auriferous.bot;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.gui.BotGUI;
import org.auriferous.bot.gui.ScriptSelector;
import org.auriferous.bot.gui.ScriptSelectorListener;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptQueue;
import org.auriferous.bot.scripts.ClickAdTask;
import org.auriferous.bot.scripts.OnAdTask;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabControlListener;
import org.auriferous.bot.tabs.Tabs;
import org.auriferous.bot.tabs.gui.TabBar;

import com.teamdev.jxbrowser.chromium.LoggerProvider;

public class Bot {
	private ScriptQueue tasks = new ScriptQueue();
	
	private JFrame botGUI = null;
	
	private Tabs tabs = null;
	
	public Bot(boolean createGUI) {
		LoggerProvider.setLevel(Level.OFF);
		
		tabs = new Tabs();
		
		if (createGUI)
			botGUI = new BotGUI(tabs);
	}

	public Tabs getTabs() {
		return tabs;
	}
	
	public Frame getBotGUI() {
		return botGUI;
	}
}
