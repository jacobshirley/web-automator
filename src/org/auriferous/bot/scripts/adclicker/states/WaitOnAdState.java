package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.scripts.adclicker.states.events.Events;

public class WaitOnAdState extends AdClickerState {
	private String adURL;
	
	public WaitOnAdState(AdClicker adClicker) {
		super(adClicker);
		this.adURL = "";
	}

	@Override
	public State process(List<Integer> events) {
		if (events.contains(Events.EVENT_PAGE_LOADED)) {
			adURL = adClicker.getBotTab().getURL();
			Task currentTask = adClicker.getCurrentTask();
			
			System.out.println("Saving URL "+adURL);
			
			System.out.println("Now waiting on ad with random 5 seconds");
			Utils.wait((currentTask.timeOnAd*1000) + Utils.random(5000));
	
			return new ClickLinksState(adClicker, adURL);
		}
		return this;
	}
}
