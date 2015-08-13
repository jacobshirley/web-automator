package org.auriferous.bot.gui.swing;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class JDebugFrame extends JFrame {
	private Browser debugger;
	private BrowserView view;
	
	public JDebugFrame(JFrame parent) {
		super("Page Debugger");
		
		setSize(900, 600);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocationRelativeTo(parent);
		
		debugger = new Browser();
		
		view = new BrowserView(debugger);
		add(view);
	}
	
	public void debug(String remoteDebuggingURL) {
		debugger.loadURL(remoteDebuggingURL);
	}
	
	public void debug(Browser browser) {
		debug(browser.getRemoteDebuggingURL());
	}
	
	public void cleanup() {
		debugger.dispose();
	}
}
