package org.auriferous.bot.script.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.tabs.Tab;

public class ScriptExecutor implements Runnable{
	private Queue<ScriptExecutorTask> tasks = new ConcurrentLinkedQueue<ScriptExecutorTask>();
	
	private Queue<Script> scripts = new ConcurrentLinkedQueue<Script>();
	private boolean started;
	
	private List<ScriptExecutionListener> listeners = new ArrayList<ScriptExecutionListener>();

	private boolean terminateScript;

	private boolean pauseScript;
	
	public ScriptExecutor(Script[] scriptsArray) {
		for (Script s : scriptsArray)
			addScript(s);
	}
	
	public void addScriptExecutionListener(ScriptExecutionListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeScriptExecutionListener(ScriptExecutionListener listener) {
		this.listeners.remove(listener);
	}
	
	public void addScript(Script script) {
		synchronized (scripts) {
			scripts.add(script);
		}
	}
	
	public void removeScript(Script script) {
		synchronized (scripts) {
			scripts.remove(script);
		}
	}

	public void resumeCurrentScript() {
		this.pauseScript = false;
	}
	
	public void pauseCurrentScript() {
		this.pauseScript = true;
	}
	
	public void terminateCurrentScript() {
		this.terminateScript = true;
	}
	
	public void stop() {
		this.started = false;
	}
	
	public void processScripts() {
		if (!this.started) {
			this.started = true;
			new Thread(this).start();
		}
	}
	
	

	@Override
	public void run() {
		while (this.started) {
			Script script = null;
			while (((script = scripts.poll()) != null)) {
				int state = 0;
				
				script.onStart();
				
				for (ScriptExecutionListener listener : listeners)
					listener.onRunScript(script);
				
				boolean terminated = false;
				while ((state = script.tick()) == Script.STATE_RUNNING) {
					if (this.pauseScript) {
						script.onPause();
						for (ScriptExecutionListener listener : listeners)
							listener.onPauseScript(script);
						
						while (this.pauseScript) {
							Thread.yield();
						}
					}
					if (this.terminateScript || !this.started) {
						terminated = true;
						this.terminateScript = false;
						script.onTerminate();
						for (ScriptExecutionListener listener : listeners)
							listener.onScriptFinished(script);
						
						break;
					}
					Thread.yield();
				}
				
				if (!terminated) {
					for (ScriptExecutionListener listener : listeners)
						listener.onScriptFinished(script);
				}
				
				System.out.println("Script exited with code: "+state);
			}
			Thread.yield();
		}
	}
}
