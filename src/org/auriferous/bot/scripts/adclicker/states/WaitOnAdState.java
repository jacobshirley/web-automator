package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;

public class WaitOnAdState extends AdClickerState {
	private String adURL;
	
	public WaitOnAdState(FSM fsm, AdClicker adClicker) {
		super(fsm, adClicker);
		this.adURL = "";
	}

	@Override
	public State process(List<Integer> events) {
		adURL = adClicker.getBotTab().getURL();
		Task currentTask = adClicker.getCurrentTask();
		
		System.out.println("Saving URL "+adURL);
		
		System.out.println("Now waiting on ad with random 5 seconds");
		Utils.wait((currentTask.timeOnAd*1000) + Utils.random(5000));

		return new ClickLinksState(fsm, adClicker, adURL);
	}
}
