package org.auriferous.bot.script;

import java.awt.Frame;

import org.auriferous.bot.Bot;
import org.auriferous.bot.input.Keyboard;
import org.auriferous.bot.input.Mouse;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.Tabs;

public class ScriptContext {
	private Bot bot;
	
	private Tabs tabs;
	
	public ScriptContext(Bot bot) {
		this.bot = bot;
		
		this.tabs = bot.getTabs();
	}
	
	public Tabs getTabs() {
		return tabs;
	}

	public Frame getBotGUI() {
		return bot.getBotGUI();
	}
}
