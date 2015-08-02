package org.auriferous.bot.script.loader;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.ScriptManifest;

public abstract class ScriptLoader {
	protected List<ScriptLibrary> libraries = new ArrayList<ScriptLibrary>();

	public ScriptLoader() {
	}
	
	public void addLibrary(ScriptLibrary library) {
		libraries.add(library);
	}
	
	public void removeLibrary(ScriptLibrary library) {
		libraries.remove(library);
	}
	
	public abstract Script loadScript(String selector);
}
