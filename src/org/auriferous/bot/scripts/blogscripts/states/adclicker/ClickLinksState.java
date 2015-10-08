package org.auriferous.bot.scripts.blogscripts.states.adclicker;

import java.awt.Point;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.scripts.blogscripts.AdClicker;
import org.auriferous.bot.scripts.blogscripts.task.Task;
import org.auriferous.bot.shared.fsm.State;
import org.auriferous.bot.shared.tabs.Tab;

public class ClickLinksState extends AdClickerState{
	private static final int MAX_CLICK_TRIES = 5;
	
	private int curLinkClick = 0;
	private int curClickTry = 0;

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
		Task currentTask = adClicker.getCurrentTask();
		ScriptMethods methods = adClicker.getScriptMethods();
		Tab botTab = adClicker.getBotTab();
		
		if (curLinkClick < currentTask.subClicks) {
			curClickTry++;
			curLinkClick++;
			
			System.out.println("Sub clicks done: "+curLinkClick);
			
			Utils.wait(2000);
			System.out.println("Clicking link in ad");

			ElementBounds randomLink = methods.getRandomClickable(adClicker.getMainFrameID(), false);
			
			if (randomLink != null) {
				adClicker.setDebugElement(randomLink);
				Point p = randomLink.getRandomPointFromCentre(0.5, 0.5);
	        	
	        	System.out.println("Clicking at "+p.x+", "+p.y);
	        	
	        	methods.mouse(p.x, p.y);
	        	adClicker.resetTimer();
	        	
	        	return new WaitOnLinkState(adClicker, adURL, curLinkClick);
			} else if (curClickTry < MAX_CLICK_TRIES){
        		System.out.println("Couldn't find link on try "+curClickTry+"/"+MAX_CLICK_TRIES+". Returning to ad to try again.");
        		adClicker.resetTimer();
        		
        		methods.scrollToRandom(true);
			} else if (curClickTry >= MAX_CLICK_TRIES) {
				System.out.println("Couldn't find link on try "+curClickTry+"/"+MAX_CLICK_TRIES+". Next task.");
				
				return new TaskDoneSignatureState(adClicker, adURL, "Couldn't find link inside ad.");
			}
		} else {
			System.out.println("Finished link clicking");
			if (!currentTask.fbLink.equals(""))
				return new PostFacebookState(adClicker, adURL);
			else
				return new TaskDoneSignatureState(adClicker, adURL, "Complete.");
		}
		
		return this;
	}
}
