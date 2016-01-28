package org.auriferous.webautomator.scripts.tests;

import org.auriferous.webautomator.script.Script;
import org.auriferous.webautomator.script.ScriptContext;
import org.auriferous.webautomator.shared.data.library.ScriptManifest;
import org.auriferous.webautomator.shared.tabs.Tab;

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
