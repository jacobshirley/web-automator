package org.auriferous.bot.scripts.blogscripts.states.adclicker;

import java.awt.Point;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.ScriptMethods.ClickType;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.scripts.blogscripts.AdClicker;
import org.auriferous.bot.scripts.blogscripts.BlogScript;
import org.auriferous.bot.scripts.blogscripts.task.Task;
import org.auriferous.bot.shared.fsm.State;
import org.auriferous.bot.shared.tabs.Tab;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class PostFacebookState extends AdClickerState{
	private String adURL;

	public PostFacebookState(AdClicker adClicker, String adURL) {
		super(adClicker);
		this.adURL = adURL;
	}

	@Override
	public State process(List<Integer> events) {
		Task currentTask = adClicker.getCurrentTask();
		System.out.println("Opening facebook page "+currentTask.fbLink);

		final Tab fbTab = adClicker.openTab(currentTask.fbLink);
		fbTab.getTabView().addPaintListener(adClicker);
		
		fbTab.getBrowserInstance().addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				if (event.isMainFrame()) {
					Utils.wait(5000);
					System.out.println("On Facebook page!!!!");
					
					ScriptMethods fbMethods = new ScriptMethods(fbTab);
					
					ElementBounds fbFoto = fbMethods.getRandomElement("$('.UFICommentPhotoIcon');");
					
					if (fbFoto != null) {
						System.out.println("Found Facebook photo");
						
						adClicker.setDebugElement(fbFoto);
						Point p = fbFoto.getRandomPointFromCentre(0.5, 0.5);

						//fbMethods.scrollTo(p.y);
						
						p.x -= 150;
						
						fbMethods.mouse(p, ClickType.LCLICK);
						Utils.wait(500);
						fbMethods.mouse(p, ClickType.LCLICK);
						Utils.wait(500);
						System.out.println("Writing signature of "+adURL);
						
						fbMethods.type(((AdClicker)adClicker).compileSignature(adURL));
					}
				}
			}
		});
		
		return new TaskDoneSignatureState((AdClicker)adClicker, adURL, "Complete.");
	}
}
