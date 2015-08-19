package org.auriferous.bot;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.auriferous.bot.config.Configurable;
import org.auriferous.bot.config.ConfigurableEntry;
import org.auriferous.bot.config.ConfigurableFile;
import org.auriferous.bot.config.library.ScriptLibrary;
import org.auriferous.bot.config.library.xml.XMLScriptLibrary;
import org.auriferous.bot.config.library.xml.XMLScriptManifest;
import org.auriferous.bot.config.xml.XMLConfigurableFile;
import org.auriferous.bot.gui.swing.JBotFrame;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.executor.ScriptExecutionListener;
import org.auriferous.bot.script.executor.ScriptExecutor;
import org.auriferous.bot.script.loader.ScriptLoader;
import org.auriferous.bot.script.loader.ScriptLoaderImpl;
import org.auriferous.bot.scripts.TestAdClicking;

import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.LoggerProvider;

public class Bot implements ScriptExecutionListener {
	private JBotFrame botGUI;
	private ScriptLibrary scriptLibrary;
	private ScriptLoader scriptLoader;
	private ScriptExecutor scriptExecutor;
	
	private ConfigurableFile config;
	
	public Bot(String args[], boolean createGUI) {
		try {
			config = new XMLConfigurableFile(new File("config/config.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LoggerProvider.setLevel(Level.OFF);
		
		BrowserPreferences.setUserAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222");
		
		scriptLibrary = new XMLScriptLibrary("Local Script Library", "1.0", "Local script library that contains the scripts on the local machine.");
		
		XMLScriptManifest manifest = new XMLScriptManifest("bin", "org.auriferous.bot.scripts.Googler", "3", "Googler", "1.0", "RAARR", "bin");
		scriptLibrary.addScript(manifest, true);
		
		XMLScriptManifest manifest2 = new XMLScriptManifest("bin", "org.auriferous.bot.scripts.adclicker.AdClicker", "4", "Ad Clicker", "1.0", "RAARR", "bin");
		scriptLibrary.addScript(manifest2, true);
		
		scriptLoader = new ScriptLoaderImpl(this, new ScriptContext(this));
		scriptLoader.addLibrary(scriptLibrary);
		
		scriptExecutor = new ScriptExecutor();
		scriptExecutor.addScriptExecutionListener(this);
		
		if (createGUI) {
			botGUI = new JBotFrame(this);
			botGUI.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					
					try {
						config.save();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}

		Script c = new TestAdClicking(manifest2, new ScriptContext(this));
		scriptExecutor.runScript(c);
	}
	
	public ConfigurableFile getConfig() {
		return config;
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

	@Override
	public void onRunScript(Script script) {
		if (script instanceof Configurable) {
			config.addConfigurable((Configurable)script);
		}
	}

	@Override
	public void onScriptFinished(Script script) {
	}

	@Override
	public void onTerminateScript(Script script) {
	}

	@Override
	public void onPauseScript(Script script) {
	}

	@Override
	public void onResumeScript(Script script) {
	}
}
