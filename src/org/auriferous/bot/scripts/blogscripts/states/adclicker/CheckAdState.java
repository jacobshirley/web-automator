package org.auriferous.bot.scripts.blogscripts.states.adclicker;

import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.scripts.blogscripts.AdClicker;
import org.auriferous.bot.scripts.blogscripts.events.Events;
import org.auriferous.bot.scripts.blogscripts.task.Task;
import org.auriferous.bot.shared.fsm.State;

import com.teamdev.jxbrowser.chromium.javafx.DefaultNetworkDelegate;

public class CheckAdState extends AdClickerState {
	private String adURL;
	private ClickAdState lastState;

	public CheckAdState(AdClicker adClicker, ClickAdState lastState) {
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
			
			adClicker.getBotTab().getBrowserInstance().getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate());
			
			System.out.println("Saving URL "+adURL);
			
			ScriptMethods methods = adClicker.getScriptMethods();
			methods.scrollToRandom(true);
			
			int time = Math.max(0, (currentTask.timeOnAd*1000) + Utils.random(-5000, 500));
			
			System.out.println("Now waiting on ad for "+(time/1000)+" seconds.");
			
			Utils.wait(time);
			adClicker.resetTimer();
			
			return new ClickLinksState(adClicker, adURL);
		}
		return this;
	}
}
