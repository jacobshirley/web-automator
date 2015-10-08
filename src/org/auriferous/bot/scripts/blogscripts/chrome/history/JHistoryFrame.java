package org.auriferous.bot.scripts.blogscripts.chrome.history;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.auriferous.bot.ResourceLoaderStatic;
import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.JDebugFrame;
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
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		file.add(new JMenuItem(new MenuAction("Refresh", 0)));
		file.addSeparator();
		file.add(new JMenuItem(new MenuAction("Exit", 1)));
		menuBar.add(file);
		
		JMenu tools = new JMenu("Tools");
		tools.add(new JMenuItem(new MenuAction("Inspect page", 2)));
		
		menuBar.add(tools);
		
		setSize(1000, 1000);
		setLocationRelativeTo(null);
		setJMenuBar(menuBar);
		setVisible(true);
	}
	
	public void refresh() {
		try {
			String pageHTML = ResourceLoaderStatic.loadResourceAsString(RESOURCES_HISTORY_FILE, true);
			
			String htmlEntry = "";
			
			List<HistoryEntry> entries = history.getEntries();
			
			SimpleDateFormat headingFormatter = new SimpleDateFormat("EEEE, d MMMM y");
			SimpleDateFormat entryFormatter = new SimpleDateFormat("hh:mm a");
			
			Date todayMidnight = getToday();
			Date yesterdayMidnight = getYesterday();
			
			String mainDate = "";
			
			for (int i = entries.size()-1; i >= 0; i--) {
				HistoryEntry entry = entries.get(i);
				
				Date date = new Date(entry.getTimeStamp());
				String tempDate = headingFormatter.format(date);
				
				if (date.after(todayMidnight)) {
					tempDate = "Today - "+tempDate;
				} else if (date.before(todayMidnight) && date.after(yesterdayMidnight)) {
					tempDate = "Yesterday - "+tempDate;
				}
				
				if (!tempDate.equals(mainDate)) {
					mainDate = tempDate;
					
					htmlEntry += "<div id='todaydate'>"+
								  mainDate+
								  "</div>";
				}
				
				if (!entry.getURL().equals("about:blank")) {
					String time = entryFormatter.format(date).toString();
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
	
	private Date getToday() {
		Calendar c = Calendar.getInstance();
		
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    
	    return c.getTime();
	}
	
	private Date getYesterday() {
		Calendar c = Calendar.getInstance();
		
	    c.add(Calendar.DATE, -1);
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    
	    return c.getTime();
	}
	
	class MenuAction extends AbstractAction {
		private int id;

		public MenuAction(String text, int id) {
			super(text);
			this.id = id;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (this.id) {
				case 0: refresh();
						break;
				case 1: JHistoryFrame.this.dispose();
						break;
				case 2: JDebugFrame debugger = new JDebugFrame(JHistoryFrame.this);
						debugger.setVisible(true);
						debugger.debug(historyBrowser);
						break;
			}
		}
	}
}
