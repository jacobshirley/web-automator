package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.scripts.adclicker.states.events.Events;

public class ShufflesState extends AdClickerState {
	private int curShuffles;
	
	public ShufflesState(AdClicker adClicker) {
		super(adClicker);
	}
	
	@Override
	public State process(List<Integer> events) {
		if (events.contains(Events.EVENT_PAGE_LOADED)) {
			Task curTask = adClicker.getCurrentTask();
			if (curShuffles < curTask.shuffles) {
				curShuffles++;
				System.out.println("Doing shuffle "+curShuffles);
				
				Utils.wait(Math.max(0, (curTask.timeInterval*1000)+Utils.random(-2000, 2000)));
				//+Utils.random(5000)
				
				adClicker.loadBlog();
				adClicker.resetTimer();
			} else {
				System.out.println("Going to clicker");
				adClicker.resetTimer();
				return new ClickAdState(adClicker, adClicker.getBotTab().getURL());
			}
		}
		
		return this;
	}
}
