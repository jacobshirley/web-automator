package org.auriferous.bot.script.input;

import java.awt.Component;

public class Input {
	protected Component target;

	public Input(Component target) {
		this.target = target;
	}
	
	public void setTarget(Component target) {
		this.target = target;
	}
	
	public Component getTarget() {
		return target;
	}
}
