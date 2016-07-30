package tech.conexus.webautomator.scripts.blogscripts.states.adclicker;

import java.awt.Point;
import java.util.List;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

import tech.conexus.webautomator.Utils;
import tech.conexus.webautomator.script.ScriptMethods;
import tech.conexus.webautomator.script.ScriptMethods.ClickType;
import tech.conexus.webautomator.script.dom.ElementBounds;
import tech.conexus.webautomator.scripts.blogscripts.AdClicker;
import tech.conexus.webautomator.scripts.blogscripts.BlogScript;
import tech.conexus.webautomator.scripts.blogscripts.task.Task;
import tech.conexus.webautomator.shared.fsm.State;
import tech.conexus.webautomator.shared.tabs.Tab;

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
