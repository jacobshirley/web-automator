package org.adclicker.bot;

import org.adclicker.bot.utils.Utils;
import org.adclicker.input.MouseSimulator;

public class Humaniser implements Runnable {
	private MouseSimulator simulator;
	private boolean started = false;
	
	public Humaniser(Bot bot) {
		this.simulator = bot.getMouseSimulator();
	}
	
	public void start() {
		this.started = true;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		while (started) {
			this.simulator.moveMouse((int)Utils.randomRange(100, 500), (int)Utils.randomRange(100, 500));
		}
	}
}