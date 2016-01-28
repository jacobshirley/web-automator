package org.auriferous.webautomator.scripts.blogscripts.states;

import org.auriferous.webautomator.scripts.blogscripts.BlogScript;
import org.auriferous.webautomator.shared.fsm.State;

public abstract class TaskState extends State{
	protected BlogScript blogScript;

	public TaskState(BlogScript blogScript) {
		this.blogScript = blogScript;
	}
}
