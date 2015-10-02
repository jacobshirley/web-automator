package org.auriferous.bot.scripts.tests;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.shared.data.library.ScriptManifest;
import org.auriferous.bot.shared.tabs.Tab;

public class TestTyping extends Script {

	private Tab botTab;


	public TestTyping(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void onStart() {
		botTab = openTab("www.google.co.uk");
	}
	
}
