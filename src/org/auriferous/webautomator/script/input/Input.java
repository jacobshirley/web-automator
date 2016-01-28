package org.auriferous.webautomator.script.input;

import org.auriferous.webautomator.shared.tabs.Tab;
import org.auriferous.webautomator.shared.tabs.view.TabView;

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
