package tech.conexus.webautomator.script.input;

import tech.conexus.webautomator.shared.tabs.Tab;
import tech.conexus.webautomator.shared.tabs.view.TabView;

public class Keyboard extends Input {
	
	public Keyboard(Tab target) {
		super(target);
	}
	
	public void typeKey(int c, int time, int mods) {
		target.getTabView().dispatchTypeKey(c, time, mods);
	}
	
	public void pressKey(int c, int mods) {
		target.getTabView().dispatchPressKey(c, mods);
	}
	
	public void releaseKey(int c, int mods) {
		target.getTabView().dispatchReleaseKey(c, mods);
	}
}
