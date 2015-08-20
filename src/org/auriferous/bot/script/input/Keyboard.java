package org.auriferous.bot.script.input;

import java.awt.Component;
import java.awt.event.KeyEvent;
import org.auriferous.bot.Utils;
import org.auriferous.bot.tabs.view.TabView;

public class Keyboard extends Input {
	
	public Keyboard(TabView target) {
		super(target);
	}
	
	public void typeKey(int c, int time, int mods) {
		target.dispatchTypeKey(c, time, mods);
	}
	
	public void pressKey(int c) {
		target.dispatchPressKey(c);
	}
	
	public void releaseKey(int c) {
		target.dispatchReleaseKey(c);
	}
}
