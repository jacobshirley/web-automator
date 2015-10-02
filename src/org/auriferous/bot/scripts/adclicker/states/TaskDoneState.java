package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.shared.fsm.State;

public class TaskDoneState extends AdClickerState {
	private String adURL;

	public TaskDoneState(AdClicker adClicker, String adURL) {
		super(adClicker);
		this.adURL = adURL;
	}

	@Override
	public State process(List<Integer> events) {
		System.out.println("Finished current task");
		
		System.out.println(adClicker.compileSignature(adURL));
		
		return new TaskNextState(adClicker);
	}
}
