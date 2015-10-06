package org.auriferous.bot.scripts.blogscripts.states.adclicker;

import java.util.List;

import org.auriferous.bot.scripts.blogscripts.AdClicker;
import org.auriferous.bot.scripts.blogscripts.BlogScript;
import org.auriferous.bot.scripts.blogscripts.states.TaskNextState;
import org.auriferous.bot.scripts.blogscripts.states.TaskState;
import org.auriferous.bot.scripts.blogscripts.task.Task;
import org.auriferous.bot.shared.fsm.State;

public class TaskDoneSignatureState extends AdClickerState {
	private String adURL;
	private String info;

	public TaskDoneSignatureState(AdClicker adClicker, String adURL, String info) {
		super(adClicker);
		this.adURL = adURL;
		this.info = info;
	}

	@Override
	public State process(List<Integer> events) {
		System.out.println("Finished current task");
		
		Task currentTask = adClicker.getCurrentTask();
		
		currentTask.status = Task.STATUS_COMPLETE;
		currentTask.info = info;
		
		System.out.println(adClicker.compileSignature(adURL));
		
		return new TaskNextState(adClicker);
	}
}
