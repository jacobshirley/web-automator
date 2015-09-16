package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;

public class TaskDoneState extends AdClickerState {
	private String adURL;

	public TaskDoneState(FSM fsm, AdClicker adClicker, String adURL) {
		super(fsm, adClicker);
		this.adURL = adURL;
	}

	@Override
	public State process(List<Integer> events) {
		System.out.println("Finished current task");
		
		System.out.println(adClicker.compileSignature(adURL));
		
		return new TaskNextState(fsm, adClicker);
	}
}