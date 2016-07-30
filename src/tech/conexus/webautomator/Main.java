package tech.conexus.webautomator;

import tech.conexus.webautomator.internal.manager.JxBrowserManager;
import tech.conexus.webautomator.internal.manager.JxBrowserManager.FinishedHandler;

public class Main {
    public static void main(String[] args) {
    	new JxBrowserManager().downloadLatest(new FinishedHandler() {
			@Override
			public void onFinish() {
				new Bot(args, true);
			}
		});
    
    	//new JxBrowserManager().downloadLatest();
    	//new HistoryConfig();
    }
}