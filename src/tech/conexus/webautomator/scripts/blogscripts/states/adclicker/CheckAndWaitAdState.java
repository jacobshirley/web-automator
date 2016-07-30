package tech.conexus.webautomator.scripts.blogscripts.states.adclicker;

import java.util.Arrays;
import java.util.List;

import com.teamdev.jxbrowser.chromium.javafx.DefaultNetworkDelegate;

import tech.conexus.webautomator.Utils;
import tech.conexus.webautomator.script.ScriptMethods;
import tech.conexus.webautomator.scripts.blogscripts.AdClicker;
import tech.conexus.webautomator.scripts.blogscripts.events.Events;
import tech.conexus.webautomator.scripts.blogscripts.task.Task;
import tech.conexus.webautomator.shared.fsm.State;

public class CheckAndWaitAdState extends AdClickerState {
	private static final int MIN_EVENT_TIME = 10000;
	
	private static final double MOUSE_EVENT_RANDOMNESS = 1.0/10;
	private static final double SCROLL_EVENT_RANDOMNESS = 6.0/10;
	
	private RandomEvent[] randomEvents;
	
	private String adURL;
	private ClickAdState lastState;

	public CheckAndWaitAdState(AdClicker adClicker, ClickAdState lastState) {
		super(adClicker);
		this.adURL = "";
		this.lastState = lastState;
	}

	@Override
	public State process(List<Integer> events) {
		if (events.contains(Events.EVENT_PAGE_LOADED)) {
			Utils.wait(2000);
			adURL = adClicker.getBotTab().getURL();
			
			Task currentTask = adClicker.getCurrentTask();
			
			if (adClicker.onBlog()) {
				adClicker.resetTimer();
				lastState.triggerError();
				System.out.println("Ad invalid. Setting appropriate state.");
				
				return lastState;
			}
			
			String base = Utils.getBaseURL(adURL);
			String title = base.split("\\.")[1];
			
			adClicker.getCurrentTask().adClicked = title;
			
			adClicker.getBotTab().getBrowserInstance().getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate());
			
			System.out.println("Saving URL "+adURL);
			
			ScriptMethods methods = adClicker.getScriptMethods();
			methods.scrollToRandom(true);
			
			randomEvents = new RandomEvent[] {new MoveMouseEvent(methods), new ScrollPageEvent(methods)};
			
			antiGoogleProtection(randomEvents, currentTask);
			
			adClicker.resetTimer();
			
			return new ClickLinksState(adClicker, adURL);
		}
		return this;
	}
	
	private void antiGoogleProtection(RandomEvent[] events, Task currentTask) {
		int time = Math.max(0, (currentTask.timeOnAd*1000) + Utils.random(-5000, 5000));
		System.out.println("Now waiting on ad for "+(time/1000)+" seconds.");
		
		long curTime = System.currentTimeMillis();
		long targetTime = curTime+time;
		
		while ((curTime = System.currentTimeMillis()) < targetTime) {
			double last = 0;
			double random = Math.random();
			System.out.println("randomness "+random);
			
			for (RandomEvent event : events) {
				double probability = event.probability();
				System.out.println("Event randomness" +event.probability());
				
				if (Utils.inRange(random, last, last+probability)) {
					System.out.println("Running event!");
					event.run();
					break;
				}
				last += probability;
			}
			Utils.wait(Math.min(MIN_EVENT_TIME, (int)(targetTime-curTime)));
		}
	}
	
	interface RandomEvent {
		public double probability();
		
		public void run();
	}
	
	class MoveMouseEvent implements RandomEvent{
		private ScriptMethods methods;

		public MoveMouseEvent(ScriptMethods methods) {
			this.methods = methods;
		}
		
		@Override
		public double probability() {
			return MOUSE_EVENT_RANDOMNESS;
		}

		@Override
		public void run() {
			methods.moveMouseRandom(true);
		}
	}
	
	class ScrollPageEvent implements RandomEvent {
		private ScriptMethods methods;

		public ScrollPageEvent(ScriptMethods methods) {
			this.methods = methods;
		}
		
		@Override
		public double probability() {
			return SCROLL_EVENT_RANDOMNESS;
		}

		@Override
		public void run() {
			methods.scrollToRandom(true);
		}
	}
}
