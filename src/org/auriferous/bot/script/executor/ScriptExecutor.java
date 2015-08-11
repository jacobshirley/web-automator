package org.auriferous.bot.script.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.tabs.Tab;

public class ScriptExecutor {
	private Map<Script, ScriptExecution> scripts = new HashMap<Script, ScriptExecution>();

	private List<ScriptExecutionListener> listeners = new ArrayList<ScriptExecutionListener>();
	
	public ScriptExecutor() {
	}
	
	public ScriptExecutor(Script[] scriptsArray) {
		for (Script s : scriptsArray)
			runScript(s);
	}
	
	public int getNumberOfScripts() {
		return scripts.size();
	}
	
	public void addScriptExecutionListener(ScriptExecutionListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeScriptExecutionListener(ScriptExecutionListener listener) {
		this.listeners.remove(listener);
	}
	
	public void runScript(Script script) {
		ScriptExecution execution = new ScriptExecution(script);
		scripts.put(script, execution);
		
		execution.start();
	}
	
	public void terminateScript(Script script) {
		scripts.get(script).stop();
	}
	
	class ScriptExecution implements Runnable {
		private Script script;

		private boolean paused = false;
		private boolean running = false;
		
		public ScriptExecution(Script script) {
			this.script = script;
		}
		
		public void start() {
			if (!this.running) {
				this.running = true;
				Thread t = new Thread(this);
				t.setName(script.getManifest().getName());
				t.start();
			}
		}
		
		public void stop() {
			this.running = false;
		}
		
		public void pause() {
			this.paused = true;
		}
		
		public void terminate() {
			script.onTerminate();
			for (ScriptExecutionListener listener : listeners)
				listener.onScriptFinished(script);
		}
		
		@Override
		public void run() {
			int state = 0;
			
			script.onStart();
			
			for (ScriptExecutionListener listener : listeners)
				listener.onRunScript(script);
			
			while ((state = script.tick()) == Script.STATE_RUNNING) {
				if (this.paused) {
					script.onPause();
					for (ScriptExecutionListener listener : listeners)
						listener.onPauseScript(script);
					
					while (this.paused) {
						if (!this.running) {
							break;
						}
						Thread.yield();
					}
				}
				
				if (!this.running) {
					terminate();
					break;
				}
				
				Thread.yield();
			}

			if (this.running) {
				for (ScriptExecutionListener listener : listeners)
					listener.onScriptFinished(script);
			}
			
			scripts.remove(script);
			
			this.running = false;
			
			System.out.println("Script exited with code: "+state);
		}
	}
	
}
