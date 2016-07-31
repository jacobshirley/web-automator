package tech.conexus.webautomator;

import tech.conexus.webautomator.internal.manager.JxBrowserManager;
import tech.conexus.webautomator.internal.manager.JxBrowserManager.FinishedHandler;

public class JxBrowserDownloader {
	public static void main(final String[] args) {
    	new JxBrowserManager().downloadLatest(new FinishedHandler() {
			@Override
			public void onFinish() {
			}
		});
    
    	//new JxBrowserManager().downloadLatest();
    	//new HistoryConfig();
    }
}
