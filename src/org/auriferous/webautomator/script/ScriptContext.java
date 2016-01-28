package org.auriferous.webautomator.script;

import org.auriferous.webautomator.Bot;
import org.auriferous.webautomator.shared.data.history.HistoryConfig;

public class ScriptContext {
	private HistoryConfig history;
	public ScriptContext(Bot bot) {
		this.history = bot.getHistoryConfig();
	}
	
	public HistoryConfig getHistory() {
		return history;
	}
}
