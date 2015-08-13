package org.auriferous.bot.script;

import javax.swing.JMenu;

import org.auriferous.bot.gui.swing.script.JGuiListener;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.Tabs;

public abstract class Script {
	public static final int STATE_RUNNING = 0;
	public static final int STATE_EXIT_SUCCESS = 1;
	public static final int STATE_EXIT_FAILURE = 2;
	
	protected ScriptContext context;
	
	protected int status = STATE_RUNNING;
	
	private Tabs tabs;
	private ScriptManifest manifest;
	
	public Script(ScriptManifest manifest, ScriptContext context) {
		this.context = context;
		this.manifest = manifest;
		this.tabs = new Tabs();
	}
	
	public ScriptManifest getManifest() {
		return manifest;
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
	
	public final Tab openTab() {
		return tabs.openTab();
	}
	
	public final Tab openTab(String url) {
		return tabs.openTab(url);
	}
}
