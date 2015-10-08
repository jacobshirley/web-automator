package org.auriferous.bot.script.input;

import org.auriferous.bot.shared.tabs.Tab;

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
