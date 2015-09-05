package org.auriferous.bot.script.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FSM {
	private Stack<State> states = new Stack<State>();
	
	public void pushState(State state) {
		states.push(state);
	}
	
	public State popState() {
		return states.pop();
	}
	
	public void tick() {
		State first = popState();
	}
}
