package org.auriferous.bot;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.tabs.Tab;

public class ScriptBundle implements Runnable{
	private Queue<Script> scripts = new ConcurrentLinkedQueue<Script>();
	private boolean started;
	private ScriptContext context;
	
	public ScriptBundle(ScriptContext context, Script[] scriptsArray) {
		this.context = context;
		
		for (Script s : scriptsArray)
			scripts.add(s);
	}
	
	public void processScripts() {
		if (!this.started) {
			this.started = true;
			new Thread(this).start();
		}
	}
	
	public void stop() {
		this.started = false;
	}

	@Override
	public void run() {
		Script s = null;
		while (((s = scripts.poll()) != null) && this.started) {
			int state = 0; 
			while ((state = s.tick()) == Script.STATE_RUNNING) {
				Thread.yield();
			}
			System.out.println("Script exited with code: "+state);
		}
		Thread.yield();
	}
}
