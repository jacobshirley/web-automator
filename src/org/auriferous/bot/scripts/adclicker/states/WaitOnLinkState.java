package org.auriferous.bot.scripts.adclicker.states;

import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.shared.fsm.State;

public class WaitOnLinkState extends AdClickerState {
	private static final int SUB_CLICK_TIME = 10;
	private static final int SUB_CLICK_RANDOM_TIME = 4;
	
	private String adURL;
	private int linkClicks;

	public WaitOnLinkState(AdClicker adClicker, String adURL, int linkClicks) {
		super(adClicker);
		this.adURL = adURL;
		this.linkClicks = linkClicks;
	}

	@Override
	public State process(List<Integer> events) {
    	System.out.println("Waiting 10 seconds + random time (0 - 4 seconds)");
    	
		Utils.wait((SUB_CLICK_TIME*1000)+Utils.random(0, SUB_CLICK_RANDOM_TIME*1000));
    	
    	System.out.println("Going back to ad "+adURL);
    	adClicker.getBotTab().loadURL(adURL);
		
		return new ClickLinksState(adClicker, adURL, linkClicks);
	}

}
