package org.auriferous.bot.scripts.blogscripts.states;

import org.auriferous.bot.scripts.blogscripts.BlogScript;
import org.auriferous.bot.shared.fsm.State;

public abstract class TaskState extends State{
	protected BlogScript blogScript;

	public TaskState(BlogScript blogScript) {
		this.blogScript = blogScript;
	}
}
