package org.auriferous.bot.scripts.blogscripts.chrome.history;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import org.auriferous.bot.ResourceLoaderStatic;
import org.auriferous.bot.Utils;
import org.auriferous.bot.shared.data.history.HistoryConfig;
import org.auriferous.bot.shared.data.history.HistoryEntry;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.LoadHTMLParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class JHistoryFrame extends JFrame{
	private static final String RESOURCES_HISTORY = "resources/blogscripts/history/";
	private static final String RESOURCES_HISTORY_FILE = RESOURCES_HISTORY+"index.html";
	
	private Browser historyBrowser;
	private BrowserView historyViewer;
	private HistoryConfig history;
	
	public JHistoryFrame(HistoryConfig history) {
		super("History");
		
		this.history = history;
		
		historyBrowser = new Browser();
		historyViewer = new BrowserView(historyBrowser);

		refresh();
		
		add(historyViewer);
		setSize(1000, 1000);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void refresh() {
		try {
			String pageHTML = ResourceLoaderStatic.loadResourceAsString(RESOURCES_HISTORY_FILE, true);
			
			String htmlEntry = "";
			
			List<HistoryEntry> entries = history.getEntries();
			SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm a");
			
			for (int i = entries.size()-1; i >= 0; i--) {
				HistoryEntry entry = entries.get(i);
				if (!entry.getURL().equals("about:blank")) {
					String time = dateFormatter.format(new Date(entry.getTimeStamp())).toString();
					String url = entry.getURL();
					
					String fav = entry.getFaviconPath();
					
					if (fav.equals("DEFAULT")) {
						fav = "http://www.google.com/s2/favicons?domain="+URLEncoder.encode(url, "UTF-8");
					} else if (fav.equals("NONE")) {
						fav = "assets/fav.png";
					}
					
					htmlEntry += "<div class='hist-line'>"+
								 "<input type='checkbox'>"+
								 "<div class='timestamp'>"+time+"</div>"+
								 "<div class='favicon'><img src='"+fav+"'></div>"+
								 "<a href='"+url+"' class='title'>"+entry.getTitle()+"</a>"+
								 "<div class='url'>"+new URL(url).getHost()+"</div>"+
								 "</div>";
				}
			}
			
			int php1 = pageHTML.indexOf("<?php");
			int php2 = pageHTML.indexOf("?>");
			
			pageHTML = pageHTML.substring(0, php1)+htmlEntry+pageHTML.substring(php2+2, pageHTML.length());

			LoadHTMLParams params = new LoadHTMLParams(pageHTML, "UTF-8", System.getProperty("user.dir")+"/"+RESOURCES_HISTORY);
			historyBrowser.loadHTML(params);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
