package org.auriferous.bot.script.library;

public interface ScriptManifest {
	public String getManifestPath();
	
	public String getID();
	public String getName();
	public String getVersion();
	public String getDescription();
	public String getFilesPath();
	
	public String getMainClass();
	
	public String getIconPath();
}
