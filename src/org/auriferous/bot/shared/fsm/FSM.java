package org.auriferous.bot.shared.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FSM {
	private Stack<State> states = new Stack<State>();
	private List<Integer> events = new ArrayList<Integer>();
	
	public boolean isFinished() {
		return states.isEmpty() || states.peek() == null;
	}
	
	public FSM clearStates() {
		states.clear();
		return this;
	}
	
	public FSM pushState(State state) {
		states.push(state);
		return this;
	}
	
	public FSM pushEvent(int event) {
		events.add(event);
		return this;
	}
	
	public synchronized FSM tick() {
		State curState = states.pop();
		State newState = curState.process(events);
		if (newState != null)
			pushState(newState);
		events.clear();
		
		return this;
	}
}
