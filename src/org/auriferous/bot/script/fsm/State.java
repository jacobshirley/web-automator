package org.auriferous.bot.script.fsm;

import java.util.List;

public abstract class State {
	protected FSM fsm;

	public State(FSM fsm) {
		this.fsm = fsm;
	}
	
	public abstract State process(List<Integer> events);
}
