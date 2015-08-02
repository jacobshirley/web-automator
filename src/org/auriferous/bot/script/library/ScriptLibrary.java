package org.auriferous.bot.script.library;

import java.io.File;

public interface ScriptLibrary {
	//Script functions
	
	public void addScript(String src);
	
	public void addScript(ScriptManifest entry);
	
	public ScriptManifest getScriptManifest(String selector);
	
	public boolean hasScript(String selector);
	
	public void removeScript(String selector);
	
	public ScriptManifest[] getScripts();
	
	//Library functions
	
	public String getName();
	
	public String getVersion();
	
	public String getDescription();
	
	public void addLibrary(String source);
	
	public void addLibrary(ScriptLibrary library);
	
	public void removeLibrary(ScriptLibrary library);
	
	public void removeLibrary(String source);
	
	public ScriptLibrary[] getLibraries();
	
	public boolean save(File file);
}
