package tech.conexus.webautomator.scripts.blogscripts.states;

import java.util.List;

import tech.conexus.webautomator.scripts.blogscripts.BlogScript;
import tech.conexus.webautomator.scripts.blogscripts.task.Task;
import tech.conexus.webautomator.shared.fsm.State;

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
