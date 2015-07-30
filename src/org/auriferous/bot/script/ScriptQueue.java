package org.auriferous.bot.script;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ScriptQueue extends PriorityQueue<Script> implements Runnable{
	private boolean started = false;
	
	public void processTasks() {
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
		while (this.started) {
			Script s = null;
			while ((s = this.poll()) != null) {
				int state = 0; 
				while ((state = s.tick()) == Script.STATE_RUNNING) {
					Thread.yield();
				}
				System.out.println("Script exited with code: "+state);
			}
			Thread.yield();
		}
	}
}
