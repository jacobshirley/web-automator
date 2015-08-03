package org.auriferous.bot.script.library;

import java.io.File;

public interface ScriptLibrary {
	//Script functions

	public void addScript(ScriptManifest manifest, boolean merge);
	
	public ScriptManifest getScriptManifest(String selector);
	
	public ScriptManifest getScriptManifestAt(int index);
	
	public boolean hasScript(String selector);
	
	public void removeScript(String selector);
	
	public ScriptManifest[] getScripts();
	
	//Library functions
	
	public String getLibraryPath();
	
	public String getName();
	
	public String getVersion();
	
	public String getDescription();

	public void addLibrary(ScriptLibrary library, boolean merge);
	
	public void removeLibrary(ScriptLibrary library);
	
	public ScriptLibrary[] getLibraries();
	
	public boolean save(File file);
}
