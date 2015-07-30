package org.adclicker.bot;

import java.awt.Frame;

import org.adclicker.bot.tabs.Tab;
import org.adclicker.bot.tabs.Tabs;
import org.adclicker.input.KeyboardSimulator;
import org.adclicker.input.MouseSimulator;

import com.teamdev.jxbrowser.chromium.Browser;

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
