package org.auriferous.bot.scripts.googler.states;

import java.util.List;

import org.auriferous.bot.scripts.googler.Googler;
import org.auriferous.bot.shared.fsm.State;

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
