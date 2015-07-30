package org.auriferous.bot.script;

import java.awt.Frame;

import org.auriferous.bot.Bot;
import org.auriferous.bot.gui.BotGUI;
import org.auriferous.bot.input.KeyboardSimulator;
import org.auriferous.bot.input.MouseSimulator;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.Tabs;

public class ScriptContext {
	private Bot bot;
	
	private Tabs tabs;
	private Tab tab;
	
	private MouseSimulator mouse;
	private KeyboardSimulator keyboard;
	
	public ScriptContext(Bot bot, Tab tab) {
		this.bot = bot;
		
		this.tabs = bot.getTabs();
		this.tab = tab;
		
		//Inputs
		this.mouse = new MouseSimulator(tab.getTabView());
		this.keyboard = new KeyboardSimulator();
		
		//Paint listeners
		tab.getTabView().addTabPaintListener(this.mouse);
	}
	
	public Tab getScriptTab() {
		return tab;
	}
	
	public Tabs getTabs() {
		return tabs;
	}

	public Frame getBotGUI() {
		return bot.getBotGUI();
	}

	public MouseSimulator getMouse() {
		return mouse;
	}
	
	public KeyboardSimulator getKeyboard() {
		return keyboard;
	}
}
