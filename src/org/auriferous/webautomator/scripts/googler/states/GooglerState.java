package org.auriferous.webautomator.scripts.googler.states;

import java.util.List;

import org.auriferous.webautomator.scripts.googler.Googler;
import org.auriferous.webautomator.shared.fsm.FSM;
import org.auriferous.webautomator.shared.fsm.State;

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
