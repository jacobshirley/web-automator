package org.auriferous.bot.scripts.blogscripts.states;

import java.util.List;

import org.auriferous.bot.scripts.blogscripts.BlogScript;
import org.auriferous.bot.scripts.blogscripts.task.Task;
import org.auriferous.bot.shared.fsm.State;

public class TaskErrorState extends TaskState {
	private int status;
	private String info;

	public TaskErrorState(BlogScript blogScript, int status, String info) {
		super(blogScript);
		this.status = status;
		this.info = info;
	}

	@Override
	public State process(List<Integer> events) {
		System.out.println("Task error: "+info+". Next task...");
		
		Task currentTask = blogScript.getCurrentTask();
		currentTask.status = status;
		currentTask.info = info;
		
		return new TaskNextState(blogScript);
	}
}
