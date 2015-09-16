package org.auriferous.bot.scripts.adclicker.states;

import java.awt.Point;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.tabs.Tab;

public class ClickLinksState extends AdClickerState{
	private int curSubClick = 0;
	private int curClickTry = 0;
	
	private String adURL;
	
	public ClickLinksState(FSM fsm, AdClicker adClicker, String adURL) {
		super(fsm, adClicker);
		this.adURL = adURL;
	}

	@Override
	public State process(List<Integer> events) {
		Task currentTask = adClicker.getCurrentTask();
		ScriptMethods methods = adClicker.getScriptMethods();
		Tab botTab = adClicker.getBotTab();
		
		if (curSubClick < currentTask.subClicks) {
			curSubClick++;
			Utils.wait(2000);
			System.out.println("Clicking link in ad");

			ElementBounds randomLink = methods.getRandomClickable(false);
			
			if (randomLink != null) {
				adClicker.setDebugElement(randomLink);
				Point p = randomLink.getRandomPointFromCentre(0.5, 0.5);
	        	
	        	System.out.println("Clicking at "+p.x+", "+p.y);
	        	
	        	methods.mouse(p.x, p.y);
	        	
	        	return new WaitOnLinkState(fsm, adClicker, adURL);
			} else if (curClickTry < 2){
				curClickTry++;
        		System.out.println("Couldn't find link on try "+curClickTry+"/2. Returning to ad to try again.");
        		
        		botTab.loadURL(adURL);
			}
		} else {
			if (currentTask.fbLink != null && !currentTask.fbLink.equals(""))
				return new PostFacebookState(fsm, adClicker, adURL);
			else
				return new TaskDoneState(fsm, adClicker, adURL);
		}
		
		return this;
	}
}
