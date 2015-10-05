package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.task.Task;
import org.auriferous.bot.shared.fsm.State;

public class TaskNextState extends AdClickerState{
	public TaskNextState(AdClicker adClicker) {
		super(adClicker);
	}

	@Override
	public State process(List<Integer> events) {
		Task currentTask = adClicker.getTasks().poll();
		adClicker.setNextTask(currentTask);
		
		if (currentTask != null) {
			System.out.println("Starting next task "+currentTask.url);
			if (adClicker.getBotTab() != null) {
				adClicker.closeTab(adClicker.getBotTab());
			}
			adClicker.handleTab();
		} else {
			System.out.println("Finished all tasks");

			return null;
		}
		
		adClicker.resetTimer();
		
		return new ShufflesState(adClicker);
	}
}
