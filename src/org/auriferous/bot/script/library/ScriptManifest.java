package org.auriferous.bot.script.library;

public interface ScriptManifest {
	public String getID();
	public String getName();
	public String getVersion();
	public String getDesc();
	public String getPath();
	
	public String getIconPath();
}
