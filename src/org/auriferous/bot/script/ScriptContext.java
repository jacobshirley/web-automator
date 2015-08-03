package org.auriferous.bot.script;

import java.awt.Frame;

import org.auriferous.bot.Bot;
import org.auriferous.bot.script.input.Keyboard;
import org.auriferous.bot.script.input.Mouse;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.Tabs;

public class ScriptContext {
	private Bot bot;
	
	private Tabs tabs;
	
	public ScriptContext(Bot bot) {
		this.bot = bot;
		
	}

	public Frame getBotGUI() {
		return bot.getBotGUI();
	}
}
