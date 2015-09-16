package org.auriferous.bot.scripts.adclicker.states;

import java.awt.Point;
import java.util.List;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.ScriptMethods.ClickType;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.script.fsm.FSM;
import org.auriferous.bot.script.fsm.State;
import org.auriferous.bot.scripts.adclicker.AdClicker;
import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.tabs.Tab;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class PostFacebookState extends AdClickerState{

	private String adURL;

	public PostFacebookState(FSM fsm, AdClicker adClicker, String adURL) {
		super(fsm, adClicker);
		this.adURL = adURL;
	}

	@Override
	public State process(List<Integer> events) {
		Task currentTask = adClicker.getCurrentTask();
		if (!currentTask.fbLink.equals("")) {
			final Tab fbTab = adClicker.openTab(currentTask.fbLink);
			
			fbTab.getBrowserInstance().addLoadListener(new LoadAdapter() {
				@Override
				public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
					if (arg0.isMainFrame()) {
						Utils.wait(5000);
						System.out.println("On Facebook page!!!!");
						
						ScriptMethods fbMethods = new ScriptMethods(fbTab);
						
						ElementBounds fbFoto = fbMethods.getRandomElement("$('.UFIReplyActorPhotoWrapper');");
						
						if (fbFoto != null) {
							System.out.println("Found Facebook photo");
							
							Point p = fbFoto.getRandomPointFromCentre(0.5, 0.5);
							
							p.x += 150;
							
							fbMethods.mouse(p, ClickType.LCLICK);
							Utils.wait(500);
							fbMethods.mouse(p, ClickType.LCLICK);
							fbMethods.type(adClicker.compileSignature(adURL));
						}
					}
				}
			});
		}
		
		return new TaskDoneState(fsm, adClicker, adURL);
	}
}
