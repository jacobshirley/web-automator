package tech.conexus.webautomator.scripts.blogscripts.states.shuffler;

import java.util.List;

import tech.conexus.webautomator.Utils;
import tech.conexus.webautomator.scripts.blogscripts.AdClicker;
import tech.conexus.webautomator.scripts.blogscripts.BlogScript;
import tech.conexus.webautomator.scripts.blogscripts.Shufflr;
import tech.conexus.webautomator.scripts.blogscripts.events.Events;
import tech.conexus.webautomator.scripts.blogscripts.states.TaskNextState;
import tech.conexus.webautomator.scripts.blogscripts.states.TaskState;
import tech.conexus.webautomator.scripts.blogscripts.states.adclicker.ClickAdState;
import tech.conexus.webautomator.scripts.blogscripts.task.Task;
import tech.conexus.webautomator.shared.data.history.HistoryEntry;
import tech.conexus.webautomator.shared.fsm.State;

public class ShufflesState extends TaskState {
	private int curShuffles;
	
	public ShufflesState(BlogScript blogScript) {
		super(blogScript);
	}
	
	@Override
	public State process(List<Integer> events) {
		if (events.contains(Events.EVENT_PAGE_LOADED)) {
			Task curTask = blogScript.getCurrentTask();
			
			String url = blogScript.getBotTab().getURL()+"#_=_";
			blogScript.getContext().getHistory().addEntry(new HistoryEntry("NONE", url, url));
			
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
