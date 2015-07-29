package org.adclicker.bot;

import com.teamdev.jxbrowser.chromium.Browser;

public class ScriptContext {
	public Bot bot;
	
	public ScriptContext(Bot bot) {
		this.bot = bot;
	}
	
	public Browser getBrowser() {
		return this.bot.getBrowser();
	}
}
