package org.auriferous.bot.scripts.blogscripts.chrome.history;

import java.io.IOException;
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
			String pageHTML = ResourceLoaderStatic.loadResourceAsString("resources/history/index.html", true);
			
			String htmlEntry = "";
			
			List<HistoryEntry> entries = history.getEntries();
			
			for (int i = entries.size()-1; i >= 0; i--) {
				HistoryEntry entry = entries.get(i);
				if (!entry.getURL().equals("about:blank")) {
					String time = new SimpleDateFormat("hh:mm a").format(new Date(entry.getTimeStamp())).toString();
					
					htmlEntry += "<div class='hist-line'>"+
								 "<input type='checkbox'>"+
								 "<div class='timestamp'>"+time+"</div>"+
								 "<div class='favicon'><img src='"+""+"'></div>"+
								 "<a href='"+entry.getURL()+"' class='title'>"+entry.getTitle()+"</a>"+
								 "<div class='url'>"+Utils.getBaseURL(entry.getURL())+"</div>"+
								 "</div>";
				}
			}
			
			int php1 = pageHTML.indexOf("<?php");
			int php2 = pageHTML.indexOf("?>");
			
			pageHTML = pageHTML.substring(0, php1)+htmlEntry+pageHTML.substring(php2+2, pageHTML.length());
		
			LoadHTMLParams params = new LoadHTMLParams(pageHTML, "UTF-8", "C:/Users/Jacob/workspace/ad-clicker-bot/resources/history/");
			historyBrowser.loadHTML(params);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
