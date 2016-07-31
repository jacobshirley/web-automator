package tech.conexus.webautomator.internal.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tech.conexus.webautomator.script.Script;

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
	
	public Set<Script> getScripts() {
		return scripts.keySet();
	}
	
	public void pauseScript(Script script) {
		scripts.get(script).pause();
	}
	
	public void resumeScript(Script script) {
		scripts.get(script).resume();
	}
	
	public void terminateScript(Script script) {
		scripts.get(script).stop();
	}
	
	class ScriptExecution implements Runnable {
		private Script script;

		private boolean paused = false;
		private boolean running = false;

		private Thread thread;
		
		public ScriptExecution(Script script) {
			this.script = script;
			this.thread = new Thread(this);
		}

		public void start() {
			if (!this.running) {
				this.running = true;
				
				thread.setName(script.getManifest().getName());
				thread.start();
			}
		}
		
		public void stop() {
			this.running = false;
		}
		
		public void pause() {
			this.paused = true;
		}
		
		public void resume() {
			this.paused = false;
		}
		
		public void terminate() {
			script.onTerminate();
			for (ScriptExecutionListener listener : listeners)
				listener.onScriptFinished(script);
		}
		
		@Override
		public void run() {
			int state = 0;
			
			for (ScriptExecutionListener listener : listeners) {
				listener.onRunScript(script);
			}
			
			script.onStart();
			
			while ((state = script.tick()) == Script.STATE_RUNNING) {
				if (this.paused) {
					for (ScriptExecutionListener listener : listeners)
						listener.onPauseScript(script);
					
					script.onPause();
					
					while (this.paused) {
						if (!this.running) {
							break;
						}
						Thread.yield();
					}
					
					for (ScriptExecutionListener listener : listeners)
						listener.onResumeScript(script);
					
					script.onResume();
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
				
				script.onFinished();
			}
			
			scripts.remove(script);
			
			this.running = false;
			
			System.out.println("Script exited with code: "+state);
		}
	}
	
}
