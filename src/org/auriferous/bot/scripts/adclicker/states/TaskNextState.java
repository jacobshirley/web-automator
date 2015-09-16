package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;

public class TaskNextState extends AdClickerState{

	public TaskNextState(FSM fsm, AdClicker adClicker) {
		super(fsm, adClicker);
	}

	@Override
	public State process(List<Integer> events) {
		adClicker.setCurrentTask(adClicker.getTasks().poll());
		
		Task currentTask = adClicker.getCurrentTask();
		
		if (currentTask != null) {
			System.out.println("Starting next task "+currentTask.url);
		} else {
			System.out.println("Finished all tasks");
			adClicker.getBotTab().alert("Finished!");
			return null;
		}
		
		return new ShufflesState(fsm, adClicker);
	}
}
