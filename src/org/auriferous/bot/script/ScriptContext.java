package org.auriferous.bot.script;

import java.awt.Frame;

import org.auriferous.bot.gui.Bot;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.Tabs;
import org.aurifierous.bot.input.KeyboardSimulator;
import org.aurifierous.bot.input.MouseSimulator;

public class ScriptContext {
	private Bot bot;
	private Frame botFrame;
	
	private Tabs tabs;
	private Tab tab;
	
	private MouseSimulator mouse;
	private KeyboardSimulator keyboard;
	
	public ScriptContext(Bot bot, Tab tab) {
		this.bot = bot;
		this.botFrame = bot.getFrame();
		
		this.tabs = bot.getTabs();
		this.tab = tab;
		
		//Inputs
		this.mouse = new MouseSimulator(tab.getTabView());
		this.keyboard = new KeyboardSimulator();
		
		//Paint listeners
		tab.getTabView().addTabPaintListener(this.mouse);
	}
	
	public Tabs getTabs() {
		return tabs;
	}
	
	public Tab getCurrentTab() {
		return tab;
	}
	
	public Frame getBotFrame() {
		return botFrame;
	}

	public MouseSimulator getMouse() {
		return mouse;
	}
	
	public KeyboardSimulator getKeyboard() {
		return keyboard;
	}
}
