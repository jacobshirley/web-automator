package org.auriferous.bot.script.fsm;

public abstract class State {
	public abstract State consume(int... events);
}
