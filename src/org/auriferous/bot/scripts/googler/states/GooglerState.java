package org.auriferous.bot.scripts.googler.states;

import java.util.List;

import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.googler.Googler;

public class GooglerState extends State {
	protected Googler googler;

	public GooglerState(Googler googler) {
		this.googler = googler;
	}

	@Override
	public State process(List<Integer> events) {
		// TODO Auto-generated method stub
		return null;
	}

}
