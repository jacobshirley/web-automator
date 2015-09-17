package org.auriferous.bot.script.fsm;

import java.util.List;

public abstract class State {
	
	public abstract State process(List<Integer> events);
}
