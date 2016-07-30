package tech.conexus.webautomator.scripts.blogscripts.states.adclicker;

import tech.conexus.webautomator.scripts.blogscripts.AdClicker;
import tech.conexus.webautomator.shared.fsm.State;

public abstract class AdClickerState extends State{
	protected AdClicker adClicker;
	
	public AdClickerState(AdClicker adClicker) {
		this.adClicker = adClicker;
	}
}
