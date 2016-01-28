package org.auriferous.webautomator.internal.loader;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.webautomator.script.Script;
import org.auriferous.webautomator.script.ScriptContext;
import org.auriferous.webautomator.shared.data.library.ScriptLibrary;
import org.auriferous.webautomator.shared.data.library.ScriptManifest;

public abstract class ScriptLoader {
	protected List<ScriptLibrary> libraries = new ArrayList<ScriptLibrary>();
	protected ScriptContext context;

	public ScriptLoader(ScriptContext context) {
		this.context = context;
	}
	
	public void setContext(ScriptContext context) {
		this.context = context;
	}
	
	public ScriptContext getContext() {
		return context;
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
