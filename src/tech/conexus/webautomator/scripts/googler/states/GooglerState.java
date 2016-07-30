package tech.conexus.webautomator.scripts.googler.states;

import java.util.List;

import tech.conexus.webautomator.scripts.googler.Googler;
import tech.conexus.webautomator.shared.fsm.FSM;
import tech.conexus.webautomator.shared.fsm.State;

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
