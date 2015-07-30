package org.auriferous.bot;

import java.awt.Frame;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;

import javax.swing.JFrame;
import org.auriferous.bot.gui.BotGUI;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.tabs.Tabs;
import com.teamdev.jxbrowser.chromium.LoggerProvider;

public class Bot {
	private JFrame botGUI = null;
	
	private Tabs tabs = null;
	
	public Bot(boolean createGUI) {
		LoggerProvider.setLevel(Level.OFF);
		
		tabs = new Tabs();
		
		if (createGUI)
			botGUI = new BotGUI(this);
	}

	public Tabs getTabs() {
		return tabs;
	}
	
	public Frame getBotGUI() {
		return botGUI;
	}
}
