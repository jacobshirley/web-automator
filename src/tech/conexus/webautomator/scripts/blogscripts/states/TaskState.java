package tech.conexus.webautomator.scripts.blogscripts.states;

import tech.conexus.webautomator.scripts.blogscripts.BlogScript;
import tech.conexus.webautomator.shared.fsm.State;

public abstract class TaskState extends State{
	protected BlogScript blogScript;

	public TaskState(BlogScript blogScript) {
		this.blogScript = blogScript;
	}
}
