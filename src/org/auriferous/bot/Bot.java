package org.auriferous.bot;

import java.awt.Frame;
import java.io.File;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;

import javax.swing.JFrame;
import org.auriferous.bot.gui.BotGUI;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.executor.ScriptExecutionListener;
import org.auriferous.bot.script.executor.ScriptExecutor;
import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.xml.XMLScriptLibrary;
import org.auriferous.bot.script.library.xml.XMLScriptManifest;
import org.auriferous.bot.script.loader.ScriptLoader;
import org.auriferous.bot.script.loader.ScriptLoaderImpl;
import org.auriferous.bot.scripts.OnAdTask;
import org.auriferous.bot.tabs.Tabs;

import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.LoggerProvider;

public class Bot {
	private BotGUI botGUI;
	private ScriptLibrary scriptLibrary;
	private ScriptLoader scriptLoader;
	private ScriptExecutor scriptExecutor;
	
	public Bot(String args[], boolean createGUI) {
		LoggerProvider.setLevel(Level.OFF);
		BrowserPreferences.setUserAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		
		scriptLibrary = new XMLScriptLibrary("library.xml");//new XMLScriptLibrary("Local Script Library", "1.0", "Local script library that contains the scripts on the local machine.");
		
		XMLScriptManifest manifest = new XMLScriptManifest("bin", "org.auriferous.bot.scripts.Googler", "3", "Googler", "1.0", "RAARR", "dsfsdf");
		scriptLibrary.addScript(manifest, true);
		
		XMLScriptManifest manifest2 = new XMLScriptManifest("bin", "org.auriferous.bot.scripts.adclicker.AdClicker", "4", "Ad Clicker", "1.0", "RAARR", "dsfsdf");
		scriptLibrary.addScript(manifest2, true);
		
		/*XMLScriptManifest manifest2 = new XMLScriptManifest("C:/Users/Jacob/workspace/Ad Clicker/bin", "org.auriferous.bot.scripts.ClickAdTask", "2", "ClickAdTask", "1.0", "RAARR", "dsfsdf");
		
		scriptLibrary.addScript(manifest, true);
		scriptLibrary.addScript(manifest2, true);
		
		scriptLibrary.save(new File("library.xml"));*/
		
		
		
		scriptLoader = new ScriptLoaderImpl(new ScriptContext(this));
		scriptLoader.addLibrary(scriptLibrary);
		
		Script c = new OnAdTask(new ScriptContext(this));
		
		scriptExecutor = new ScriptExecutor(new Script[] {c});
		scriptExecutor.processScripts();
		
		if (createGUI)
			botGUI = new BotGUI(this);
		
		botGUI.tabBar.addTabs(c.getTabs());
		
		/*try {
			Script s = scriptLoader.loadScript(scriptLibrary.getScriptManifest("1"));
			scriptExecutor.addScript(s);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public ScriptLibrary getScriptLibrary() {
		return scriptLibrary;
	}
	
	public ScriptLoader getScriptLoader() {
		return scriptLoader;
	}
	
	public ScriptExecutor getScriptExecutor() {
		return scriptExecutor;
	}
	
	public JFrame getBotGUI() {
		return botGUI;
	}
}
