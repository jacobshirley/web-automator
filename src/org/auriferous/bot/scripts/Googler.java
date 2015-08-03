package org.auriferous.bot.scripts;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptMethods;
import org.auriferous.bot.tabs.Tab;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;

public class Googler extends Script{
	private ScriptMethods methods;

	public Googler(ScriptContext context) {
		super(context);
	}

	@Override
	public void onStart() {
		final Tab googleTab = openTab("www.google.co.uk");
		
		googleTab.getBrowserWindow().addLoadListener(new LoadAdapter() {
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				methods = new ScriptMethods(googleTab);
				methods.type("What the fuck?");
				status = STATE_EXIT_SUCCESS;
			}
		});
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onTerminate() {
	}
}
