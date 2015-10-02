package org.auriferous.bot.script;

import org.auriferous.bot.Bot;
import org.auriferous.bot.shared.data.history.HistoryConfig;

public class ScriptContext {
	private HistoryConfig history;
	public ScriptContext(Bot bot) {
		this.history = bot.getHistoryConfig();
	}
	
	public HistoryConfig getHistory() {
		return history;
	}
}
