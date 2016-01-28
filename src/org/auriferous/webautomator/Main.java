package org.auriferous.webautomator;

import java.awt.BorderLayout;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.auriferous.webautomator.scripts.blogscripts.states.adclicker.ClickAdState;
import org.auriferous.webautomator.shared.data.history.HistoryConfig;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class Main {
    public static void main(String[] args) {
    	new Bot(args, true);
    	//new HistoryConfig();
    }
}