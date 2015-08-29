package org.auriferous.bot.scripts;

import java.awt.event.KeyEvent;

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
	private static final int STAGE_GOOGLE = 0;
	private static final int STAGE_CLICK_LINK = 1;
	private static final int STAGE_SAVE_URL = 2;
	private static final int STAGE_CLICK_SUB_LINKS = 3;
	private static final int STAGE_RETURN_TO_LINK = 4;
	
	private static final int MAX_CLICKS = 6;
	
	private int stage;
	
	private ScriptMethods methods;
	private boolean exec = false;

	public Googler(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}

	private Tab googleTab = null;
	
	@Override
	public void onStart() {
		googleTab = openTab("www.google.co.uk");
		
		googleTab.getBrowserWindow().addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				exec = true;
			}
		});
	}
	
	private boolean tickGoogle() {
		System.out.println("Started typing");
		
		methods = new ScriptMethods(googleTab);
		methods.type("I like to Google stuff.");
		methods.type(KeyEvent.VK_ENTER);
		
		Utils.wait(2000);
		ElementBounds el = methods.getRandomElement("$('.g').find('.r').find('a')");
		methods.scrollTo((int)el.getCenterY(), 1000, 2000);
		methods.mouse((int)el.getCenterX(), (int)el.getCenterY());
		
		stage = STAGE_SAVE_URL;
		
		return true;
	}
	
	private int subClicks = 0;
	private String saveURL = "";
	private ElementBounds[] elements = null;
	
	private boolean tickSaveURL() {
		saveURL = googleTab.getURL();
		
		elements = methods.getElements("$(document).findVisibles('a[href^=\"http\"], a[href^=\"/\"]');");
		
		stage = STAGE_CLICK_SUB_LINKS;
		
		return false;
	}
	
	private boolean tickSubClicks() {
		if (subClicks < MAX_CLICKS) {
			subClicks++;
			System.out.println("Started clicking links "+subClicks+"/"+MAX_CLICKS);
			ElementBounds clickable = methods.getRandomClickable(false);
			if (clickable != null) {
				methods.mouse(clickable.getRandomPointFromCentre(0.5, 0.5));
			}
			stage = STAGE_RETURN_TO_LINK;
		} else {
			status = STATE_EXIT_SUCCESS;
		}
		
		return true;
	}
	
	private boolean tickReturn() {
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
				case STAGE_GOOGLE: if (tickGoogle()) break;
				case STAGE_SAVE_URL: if (tickSaveURL()) break;
				case STAGE_CLICK_SUB_LINKS: if (tickSubClicks()) break;
				case STAGE_RETURN_TO_LINK: if (tickReturn()) break;
			}
			timer = System.currentTimeMillis();
			exec = false;
		} else {
			if (System.currentTimeMillis()-timer >= 5000) {
				System.out.println("It's been five seconds. Forcing execution.");
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
