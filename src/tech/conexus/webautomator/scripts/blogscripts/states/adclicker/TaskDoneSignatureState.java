package tech.conexus.webautomator.scripts.blogscripts.states.adclicker;

import java.util.List;

import tech.conexus.webautomator.scripts.blogscripts.AdClicker;
import tech.conexus.webautomator.scripts.blogscripts.BlogScript;
import tech.conexus.webautomator.scripts.blogscripts.states.TaskNextState;
import tech.conexus.webautomator.scripts.blogscripts.states.TaskState;
import tech.conexus.webautomator.scripts.blogscripts.task.Task;
import tech.conexus.webautomator.shared.fsm.State;

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
