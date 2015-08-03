package org.auriferous.bot.script;

import org.auriferous.bot.tabs.Tab;

public abstract class Script {
	public static final int STATE_RUNNING = 0;
	public static final int STATE_EXIT_SUCCESS = 1;
	protected ScriptContext context;
	
	public Script(ScriptContext context) {
		this.context = context;
	}
	
	public abstract int tick();
	
	public abstract void onStart();
	public abstract void onPause();
	public abstract void onTerminate();
	
	public final Tab openTab(String url) {
		return context.getTabs().openTab(url, this);
	}
}
