package org.auriferous.bot.script.loader;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.bot.data.library.ScriptLibrary;
import org.auriferous.bot.data.library.ScriptManifest;
import org.auriferous.bot.script.Script;

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
	
	public Script loadScript(String selector) throws ClassNotFoundException {
		for (ScriptLibrary library : libraries) {
			ScriptManifest manifest = library.getScriptManifest(selector);
			if (manifest != null) {
				return loadScript(manifest);
			}
		}
		return null;
	}
	
	public abstract Script loadScript(ScriptManifest manifest) throws ClassNotFoundException;
}
