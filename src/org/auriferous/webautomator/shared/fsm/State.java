package org.auriferous.webautomator.shared.fsm;

import java.util.List;

public abstract class State {
	public abstract State process(List<Integer> events);
}
