package org.auriferous.bot.scripts.blogscripts.states.shuffler;

import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.scripts.blogscripts.AdClicker;
import org.auriferous.bot.scripts.blogscripts.BlogScript;
import org.auriferous.bot.scripts.blogscripts.Shufflr;
import org.auriferous.bot.scripts.blogscripts.events.Events;
import org.auriferous.bot.scripts.blogscripts.states.TaskNextState;
import org.auriferous.bot.scripts.blogscripts.states.TaskState;
import org.auriferous.bot.scripts.blogscripts.states.adclicker.ClickAdState;
import org.auriferous.bot.scripts.blogscripts.task.Task;
import org.auriferous.bot.shared.fsm.State;

public class ShufflesState extends TaskState {
	private int curShuffles;
	
	public ShufflesState(BlogScript blogScript) {
		super(blogScript);
	}
	
	@Override
	public State process(List<Integer> events) {
		if (events.contains(Events.EVENT_PAGE_LOADED)) {
			Task curTask = blogScript.getCurrentTask();
			if (curShuffles < curTask.shuffles) {
				curShuffles++;
				System.out.println("Doing shuffle "+curShuffles);
				
				Utils.wait(Math.max(0, (curTask.timeInterval*1000)+Utils.random(-2000, 2000)));
				//+Utils.random(5000)
				
				blogScript.loadBlog();
				blogScript.resetTimer();
			} else {
				System.out.println("Going to clicker");
				blogScript.resetTimer();
				blogScript.saveBlogURL();
				
				if (blogScript instanceof AdClicker) {
					return new ClickAdState((AdClicker)blogScript, blogScript.getBotTab().getURL());
				} else if (blogScript instanceof Shufflr) {
					return new TaskNextState(blogScript);
				}
			}
		}
		
		return this;
	}
}
