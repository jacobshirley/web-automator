package tech.conexus.webautomator.shared.tabs;

import com.teamdev.jxbrowser.chromium.Browser;

public interface TabListener {
	public void onTitleChange(String newTitle);
	
	public void onTabUpdating();
	
	public void onTabReloaded();
	
	public void onTabBrowserChanged(Browser browser);

	public void onTabClosed();
}
