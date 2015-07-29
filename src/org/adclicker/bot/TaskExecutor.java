package org.adclicker.bot;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.adclicker.bot.tasks.BotTask;

public class TaskExecutor implements Runnable{
	private Queue<BotTask> taskQueue = new ConcurrentLinkedQueue<BotTask>();
	private boolean started = false;

	public void addTask(BotTask task) {
		this.taskQueue.add(task);
	}
	
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
			BotTask t = null;
			while ((t = taskQueue.poll()) != null) {
				t.perform();
			}
			Thread.yield();
		}
	}
}
