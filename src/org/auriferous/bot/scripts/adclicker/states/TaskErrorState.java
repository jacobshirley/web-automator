package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.task.Task;
import org.auriferous.bot.shared.fsm.State;

public class TaskErrorState extends AdClickerState {
	private int status;
	private String info;

	public TaskErrorState(AdClicker adClicker, int status, String info) {
		super(adClicker);
		this.status = status;
		this.info = info;
	}

	@Override
	public State process(List<Integer> events) {
		System.out.println("Task error: "+info+". Next task...");
		
		Task currentTask = adClicker.getCurrentTask();
		currentTask.status = status;
		currentTask.info = info;
		
		return new TaskNextState(adClicker);
	}
}
