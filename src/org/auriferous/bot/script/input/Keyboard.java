package org.auriferous.bot.script.input;

import org.auriferous.bot.tabs.view.TabView;

public class Keyboard extends Input {
	
	public Keyboard(TabView target) {
		super(target);
	}
	
	public void typeKey(int c, int time, int mods) {
		target.dispatchTypeKey(c, time, mods);
	}
	
	public void pressKey(int c, int mods) {
		target.dispatchPressKey(c, mods);
	}
	
	public void releaseKey(int c, int mods) {
		target.dispatchReleaseKey(c, mods);
	}
}
