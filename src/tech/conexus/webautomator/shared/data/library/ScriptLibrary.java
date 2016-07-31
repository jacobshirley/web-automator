package tech.conexus.webautomator.shared.data.library;

import java.io.File;

import tech.conexus.webautomator.shared.data.library.ScriptLibrary.FilterType;

public abstract class ScriptLibrary {
	
	public ScriptLibrary() {
		
	}
	
	public String getLibraryPath() {
		return null;
	}
	
	public String getName() {
		return null;
	}
	
	public String getVersion() {
		return null;
	}
	
	public String getDescription() {
		return null;
	}
	
	//Script functions

	public abstract void addScript(ScriptManifest manifest, boolean merge);
	
	public abstract ScriptManifest getScriptManifest(String selector, FilterType filter);
	
	public abstract ScriptManifest getScriptManifestAt(int index);
	
	public abstract boolean hasScript(String selector, FilterType filterType);
	
	public abstract void removeScript(String selector, FilterType filterType);
	
	public abstract ScriptManifest[] getScripts();

	//Library functions
	
	public abstract void addLibrary(ScriptLibrary library, boolean merge);
	
	public abstract void removeLibrary(ScriptLibrary library);
	
	public abstract ScriptLibrary[] getLibraries();
	
	public abstract boolean save(File file);
	
	public enum FilterType {ID, NAME};
}
