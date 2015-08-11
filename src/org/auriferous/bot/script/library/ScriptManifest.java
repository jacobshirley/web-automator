package org.auriferous.bot.script.library;

public abstract class ScriptManifest {
	protected String src = "";
	
	public ScriptManifest() {
	}

	public ScriptManifest(String src) {
		this.src = src;
	}
	 
	public String getManifestSrc() {
		return src;
	}
	
	public abstract String getID();
	public abstract String getName();
	public abstract String getVersion();
	public abstract String getDescription();
	public abstract String getFilesPath();
	
	public abstract String getMainClass();
	
	public abstract String getIconPath();
}
