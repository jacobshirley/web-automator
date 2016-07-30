package tech.conexus.webautomator.scripts.tests;

import tech.conexus.webautomator.script.Script;
import tech.conexus.webautomator.script.ScriptContext;
import tech.conexus.webautomator.shared.data.library.ScriptManifest;
import tech.conexus.webautomator.shared.tabs.Tab;

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
