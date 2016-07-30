package tech.conexus.webautomator.scripts.blogscripts.states;

import java.util.List;

import tech.conexus.webautomator.scripts.blogscripts.BlogScript;
import tech.conexus.webautomator.scripts.blogscripts.states.shuffler.ShufflesState;
import tech.conexus.webautomator.scripts.blogscripts.task.Task;
import tech.conexus.webautomator.shared.fsm.State;

public class TaskNextState extends TaskState{
	public TaskNextState(BlogScript blogScript) {
		super(blogScript);
	}

	@Override
	public State process(List<Integer> events) {
		Task currentTask = blogScript.getTasks().poll();
		blogScript.setNextTask(currentTask);
		
		if (currentTask != null) {
			System.out.println("Starting next task "+currentTask.url);
			if (blogScript.getBotTab() != null) {
				blogScript.closeTab(blogScript.getBotTab());
			}
			blogScript.handleTab();
		} else {
			System.out.println("Finished all tasks");

			return null;
		}
		
		blogScript.resetTimer();
		
		return new ShufflesState(blogScript);
	}
}
