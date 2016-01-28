package org.auriferous.webautomator.scripts.blogscripts.states.adclicker;

import org.auriferous.webautomator.scripts.blogscripts.AdClicker;
import org.auriferous.webautomator.shared.fsm.State;

public abstract class AdClickerState extends State{
	protected AdClicker adClicker;
	
	public AdClickerState(AdClicker adClicker) {
		this.adClicker = adClicker;
	}
}
