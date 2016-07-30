package tech.conexus.webautomator.script.input;

import tech.conexus.webautomator.shared.tabs.Tab;
import tech.conexus.webautomator.shared.tabs.view.TabView;

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
