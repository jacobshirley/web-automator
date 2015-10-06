package org.auriferous.bot.scripts.blogscripts.states.adclicker;

import org.auriferous.bot.scripts.blogscripts.AdClicker;
import org.auriferous.bot.shared.fsm.State;

public abstract class AdClickerState extends State{
	protected AdClicker adClicker;
	
	public AdClickerState(AdClicker adClicker) {
		this.adClicker = adClicker;
	}
}
