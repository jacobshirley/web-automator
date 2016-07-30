package tech.conexus.webautomator.script;

import tech.conexus.webautomator.Bot;
import tech.conexus.webautomator.shared.data.history.HistoryConfig;

public class ScriptContext {
	private HistoryConfig history;
	public ScriptContext(Bot bot) {
		this.history = bot.getHistoryConfig();
	}
	
	public HistoryConfig getHistory() {
		return history;
	}
}
