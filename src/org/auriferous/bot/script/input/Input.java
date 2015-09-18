package org.auriferous.bot.script.input;

import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.view.TabView;

public class Input {
	protected Tab target;

	public Input(Tab target) {
		this.target = target;
	}
	
	public void setTarget(Tab target) {
		this.target = target;
	}
	
	public Tab getTarget() {
		return target;
	}
}
