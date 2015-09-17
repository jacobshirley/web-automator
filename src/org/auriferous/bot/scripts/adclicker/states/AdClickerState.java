package org.auriferous.bot.scripts.adclicker.states;

import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;

public abstract class AdClickerState extends State{
	protected AdClicker adClicker;

	public AdClickerState(AdClicker adClicker) {
		this.adClicker = adClicker;
	}
}
