package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.scripts.adclicker.states.events.Events;

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
			
			adURL = adClicker.getBotTab().getURL();
			
			Task currentTask = adClicker.getCurrentTask();
			
			if (adURL.contains(Utils.getBaseURL(lastState.getCurrentTaskURL()))) {
				lastState.triggerError();
				System.out.println("Gone back to blog. Setting appropriate state.");
				return lastState;
			}
			
			adClicker.getBotTab().getBrowserInstance().getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate());
			
			System.out.println("Saving URL "+adURL);
			
			System.out.println("Now waiting on ad with random 5 seconds");
			
			ScriptMethods methods = adClicker.getScriptMethods();
			int y = (int)Math.round(Math.random()*methods.getPageHeight());
			
			System.out.println("Scrolling to random "+y);
			methods.scrollTo(y);
			Utils.wait((currentTask.timeOnAd*1000) + Utils.random(5000));
			
			return new ClickLinksState(adClicker, adURL);
		}
		return this;
	}
}
