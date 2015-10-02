package org.auriferous.bot.scripts.adclicker.states;

import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.shared.fsm.State;

public abstract class AdClickerState extends State{
	protected AdClicker adClicker;

	public AdClickerState(AdClicker adClicker) {
		this.adClicker = adClicker;
	}
}
