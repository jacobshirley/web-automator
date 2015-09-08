package org.auriferous.bot.scripts.googler;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;

import org.auriferous.bot.Utils;
import org.auriferous.bot.config.library.ScriptManifest;
import org.auriferous.bot.gui.swing.script.JScriptGuiListener;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.script.dom.ElementBounds;
import org.auriferous.bot.tabs.Tab;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class Googler extends Script implements JScriptGuiListener{
	private static final String[] SEARCHES = new String[] {"insurance marketplace", "insurance quotes", "insurance companies", "interest rates", "restaurants in Brighton", "pubs in Brighton"};
	
	private static final int STAGE_GOOGLE = 0;
	private static final int STAGE_CLICK_LINK = 1;
	private static final int STAGE_SAVE_URL = 2;
	private static final int STAGE_CLICK_SUB_LINKS = 3;
	private static final int STAGE_RETURN_TO_LINK = 4;
	private static final int STAGE_NEXT_SEARCH = 5;
	
	private static final int MAX_CLICKS = 6;

	private int stage;
	
	private ScriptMethods methods;
	private boolean exec = false;
	
	private long mainFrame = 0;
	
	private List<String> searches = new LinkedList<String>();

	public Googler(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
		
		searches.addAll(Arrays.asList(SEARCHES));
	}

	private Tab googleTab = null;
	
	@Override
	public void onStart() {
		googleTab = openTab("www.google.co.uk");
		
		googleTab.getBrowserWindow().addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				exec = true;
				mainFrame = event.getFrameId();
			}
		});
		methods = new ScriptMethods(googleTab);
		
		timer = System.currentTimeMillis();
	}
	
	private boolean tickNextSearch() {
		return false;
	}
	
	private boolean tickGoogle() {
		if (!searches.isEmpty()) {
			Utils.wait(5000);
			
			System.out.println("Started typing");
			int random = (int)Math.floor(searches.size()*Math.random());
			String search = searches.remove(random);
			
			System.out.println("Looking for input element");
			
			methods.clickElement(methods.getRandomElement(mainFrame, "$('#lst-ib');"));
			System.out.println("Found input element");
			
			methods.type(search);
			methods.type(KeyEvent.VK_ENTER);
			
			Utils.wait(2000);
			
			ElementBounds el = methods.getRandomElement("$('.g').find('.r').find('a')");
			methods.scrollTo((int)el.getCenterY(), 500, 500);
			methods.mouse((int)el.getCenterX(), (int)el.getCenterY());
			
			stage = STAGE_SAVE_URL;
			
			return true;
		} else {
			System.out.println("All finished. Exiting...");
			status = STATE_EXIT_SUCCESS;
			
			return true;
		}
	}
	
	private int subClicks = 0;
	private String saveURL = "";
	private ElementBounds[] elements = null;
	
	private int getWaitTime(ElementBounds[] elements) {
		int time = 6;
		if (elements != null) {
			time = (int) (elements.length*0.5);
			if (time > 16)
				time = 16;
			else if (time < 5)
				time = 5;
		}
		return (time+Utils.random(-5, 5))*1000;
	}
	
	private int getWaitTime() {
		return getWaitTime(methods.getElements("$(document).findVisibles('a[href^=\"http\"], a[href^=\"/\"]');"));
	}
	
	private boolean tickSaveURL() {
		subClicks = 0;
		saveURL = googleTab.getURL();
		
		elements = methods.getElements("$(document).findVisibles('a[href^=\"http\"], a[href^=\"/\"]');");
		if (elements == null) {
			Utils.wait(5000);
			elements = methods.getElements("$(document).findVisibles('a[href^=\"http\"], a[href^=\"/\"]');");
		}
		int waitTime = getWaitTime(elements);
		System.out.println("Waiting "+(waitTime/1000)+" seconds");
		Utils.wait(waitTime);
		
		stage = STAGE_CLICK_SUB_LINKS;
		
		return false;
	}
	
	private boolean tickSubClicks() {
		if (subClicks < MAX_CLICKS) {
			subClicks++;
			System.out.println("Started clicking links "+subClicks+"/"+MAX_CLICKS);
			
			ElementBounds clickable = (ElementBounds)Utils.getRandomObject(elements);
			if (clickable != null) {
				methods.mouse(clickable.getRandomPointFromCentre(0.7, 0.7));
			} else {
				System.out.println("couldn't find link");
			}
			stage = STAGE_RETURN_TO_LINK;
			
			return true;
		} else {
			System.out.println("Going to google");
			
			stage = STAGE_GOOGLE;
			googleTab.loadURL("www.google.co.uk");
			return true;
		}
	}
	
	private boolean tickReturn() {
		int waitTime = getWaitTime();
		System.out.println("Waiting "+(waitTime/1000)+" seconds");
		Utils.wait(waitTime);
		
		methods.moveMouseRandom();
		
		System.out.println("Returing to original link");
		googleTab.loadURL(saveURL);
		
		stage = STAGE_CLICK_SUB_LINKS;
		
		return true;
	}
	
	private long timer = 0;
	
	@Override
	public int tick() {
		if (exec) {
			switch (stage) {
				case STAGE_NEXT_SEARCH: if (tickNextSearch()) break;
				case STAGE_GOOGLE: if (tickGoogle()) break;
				case STAGE_SAVE_URL: if (tickSaveURL()) break;
				case STAGE_CLICK_SUB_LINKS: if (tickSubClicks()) break;
				case STAGE_RETURN_TO_LINK: if (tickReturn()) break;
			}
			timer = System.currentTimeMillis();
			exec = false;
		} else {
			if (System.currentTimeMillis()-timer >= 10000) {
				System.out.println("It's been 10 seconds. Forcing execution.");
				exec = true;
			}
		}

		return super.tick();
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onTerminate() {
	}

	@Override
	public void onJMenuCreated(JMenu menu) {
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onFinished() {
	}
}
