package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;

public class WaitOnLinkState extends AdClickerState {
	private static final int SUB_CLICK_TIME = 10;
	private static final int SUB_CLICK_RANDOM_TIME = 4;
	
	private String adURL;

	public WaitOnLinkState(FSM fsm, AdClicker adClicker, String adURL) {
		super(fsm, adClicker);
		this.adURL = adURL;
	}

	@Override
	public State process(List<Integer> events) {
    	System.out.println("Waiting 10 seconds + random time (0 - 4 seconds)");
    	
		Utils.wait((SUB_CLICK_TIME*1000)+Utils.random(0, SUB_CLICK_RANDOM_TIME*1000));
    	
    	System.out.println("Going back to ad "+adURL);
    	adClicker.getBotTab().loadURL(adURL);
		
		return new ClickLinksState(fsm, adClicker, adURL);
	}

}
