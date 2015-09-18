package org.auriferous.bot.scripts.adclicker.states;

import java.awt.Point;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.scripts.adclicker.states.events.Events;
import org.auriferous.bot.tabs.Tab;

public class ClickLinksState extends AdClickerState{
	private static final int MAX_CLICK_TRIES = 2;
	
	private int curLinkClick = 0;
	private int curClickTry = 0;
	
	private boolean waitForLoad = false;
	
	private String adURL;
	
	public ClickLinksState(AdClicker adClicker, String adURL) {
		super(adClicker);
		this.adURL = adURL;
	}
	
	public ClickLinksState(AdClicker adClicker, String adURL, int curLinkClick) {
		this(adClicker, adURL);
		this.curLinkClick = curLinkClick;
	}

	@Override
	public State process(List<Integer> events) {
		if (waitForLoad && !events.contains(Events.EVENT_PAGE_LOADED))
			return this;
		
		Task currentTask = adClicker.getCurrentTask();
		ScriptMethods methods = adClicker.getScriptMethods();
		Tab botTab = adClicker.getBotTab();
		
		if (curLinkClick < currentTask.subClicks) {
			System.out.println("Sub clicked done: "+curLinkClick);
			
			Utils.wait(2000);
			System.out.println("Clicking link in ad");

			ElementBounds randomLink = methods.getRandomClickable(false);
			
			if (randomLink != null) {
				curLinkClick++;
				adClicker.setDebugElement(randomLink);
				Point p = randomLink.getRandomPointFromCentre(0.5, 0.5);
	        	
	        	System.out.println("Clicking at "+p.x+", "+p.y);
	        	
	        	methods.mouse(p.x, p.y);
	        	
	        	return new WaitOnLinkState(adClicker, adURL, curLinkClick);
			} else if (curClickTry < MAX_CLICK_TRIES){
				curClickTry++;
        		System.out.println("Couldn't find link on try "+curClickTry+"/"+MAX_CLICK_TRIES+". Returning to ad to try again.");
        		
        		botTab.loadURL(adURL);
        		
        		waitForLoad = true;
			} else if (curClickTry == MAX_CLICK_TRIES) {
				return new TaskDoneState(adClicker, adURL);
			}
		} else {
			if (!currentTask.fbLink.equals(""))
				return new PostFacebookState(adClicker, adURL);
			else
				return new TaskDoneState(adClicker, adURL);
		}
		
		return this;
	}
}
