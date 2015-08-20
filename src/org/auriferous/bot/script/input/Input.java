package org.auriferous.bot.script.input;

import java.awt.Component;

import org.auriferous.bot.tabs.view.TabView;

public class Input {
	protected TabView target;

	public Input(TabView target) {
		this.target = target;
	}
	
	public void setTarget(TabView target) {
		this.target = target;
	}
	
	public TabView getTarget() {
		return target;
	}
}
