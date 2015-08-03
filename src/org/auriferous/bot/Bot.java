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
import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.xml.XMLScriptLibrary;
import org.auriferous.bot.script.library.xml.XMLScriptManifest;
import org.auriferous.bot.script.loader.ScriptLoader;
import org.auriferous.bot.script.loader.ScriptLoaderImpl;
import org.auriferous.bot.tabs.Tabs;
import com.teamdev.jxbrowser.chromium.LoggerProvider;

public class Bot {
	private JFrame botGUI;
	private Tabs tabs;
	private ScriptLibrary scriptLibrary;
	private ScriptLoader scriptLoader;
	
	public Bot(boolean createGUI) {
		LoggerProvider.setLevel(Level.OFF);
		
		tabs = new Tabs();
		
		scriptLibrary = new XMLScriptLibrary("library.xml");
		
		XMLScriptManifest manifest = new XMLScriptManifest("C:/Users/Jacob/workspace/Ad Clicker/bin", "org.auriferous.bot.scripts.OnAdTask", "1", "hi there", "1.0", "RAARR", "dsfsdf");
		
		scriptLibrary.addScript(manifest, true);
		
		//scriptLibrary.save(new File("library.xml"));
		
		scriptLoader = new ScriptLoaderImpl(new ScriptContext(this));
		scriptLoader.addLibrary(scriptLibrary);
		
		if (createGUI)
			botGUI = new BotGUI(this);
	}
	
	public ScriptLibrary getScriptLibrary() {
		return scriptLibrary;
	}
	
	public ScriptLoader getScriptLoader() {
		return scriptLoader;
	}

	public Tabs getTabs() {
		return tabs;
	}
	
	public Frame getBotGUI() {
		return botGUI;
	}
}
