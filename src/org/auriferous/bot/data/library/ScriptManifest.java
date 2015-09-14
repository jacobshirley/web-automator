package org.auriferous.bot.data.library;

public abstract class ScriptManifest {
	protected String src = "";
	private ScriptLibrary owner;
	
	public ScriptManifest() {
	}

	public ScriptManifest(String src) {
		this.src = src;
	}
	
	public ScriptManifest(ScriptLibrary owner, String src) {
		this.src = src;
		this.owner = owner;
	}
	 
	public String getManifestSrc() {
		return src;
	}
	
	public void setLibrary(ScriptLibrary owner) {
		this.owner = owner;
	}
	
	public ScriptLibrary getLibrary() {
		return owner;
	}
	
	public abstract String getID();
	public abstract String getName();
	public abstract String getVersion();
	public abstract String getDescription();
	public abstract String getFilesPath();
	
	public abstract String getMainClass();
	
	public abstract String getIconPath();
}
