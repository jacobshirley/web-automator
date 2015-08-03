package org.auriferous.bot.script;

import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.Tabs;

public abstract class Script {
	public static final int STATE_RUNNING = 0;
	public static final int STATE_EXIT_SUCCESS = 1;
	protected ScriptContext context;
	
	protected int status = STATE_RUNNING;
	
	private Tabs tabs;
	
	public Script(ScriptContext context) {
		this.context = context;
		this.tabs = new Tabs();
	}
	
	public Tabs getTabs() {
		return tabs;
	}
	
	public int tick() {
		return status;
	}
	
	public abstract void onStart();
	public abstract void onPause();
	public abstract void onTerminate();
	
	public final Tab openTab(String url) {
		return tabs.openTab(url, this);
	}
}
