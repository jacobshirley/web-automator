package tech.conexus.webautomator.internal.loader;

import java.util.ArrayList;
import java.util.List;

import tech.conexus.webautomator.script.Script;
import tech.conexus.webautomator.script.ScriptContext;
import tech.conexus.webautomator.shared.data.library.ScriptLibrary;
import tech.conexus.webautomator.shared.data.library.ScriptLibrary.FilterType;
import tech.conexus.webautomator.shared.data.library.ScriptManifest;

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
	
	public Script loadScript(String selector, FilterType filterType) throws ClassNotFoundException {
		for (ScriptLibrary library : libraries) {
			ScriptManifest manifest = library.getScriptManifest(selector, filterType);
			if (manifest != null) {
				return loadScript(manifest);
			}
		}
		return null;
	}
	
	public abstract Script loadScript(ScriptManifest manifest) throws ClassNotFoundException;
}
