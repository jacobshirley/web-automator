package org.auriferous.bot.gui.scriptselector;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.library.ScriptManifest;

public interface ScriptSelectorListener {
	public void onScriptSelected(Script script);
}
