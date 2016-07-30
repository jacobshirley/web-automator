package tech.conexus.webautomator;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JFrame;

import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.LoggerProvider;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;

import tech.conexus.webautomator.gui.swing.JBotFrame;
import tech.conexus.webautomator.internal.executor.ScriptExecutionListener;
import tech.conexus.webautomator.internal.executor.ScriptExecutor;
import tech.conexus.webautomator.internal.loader.CachedScriptLoader;
import tech.conexus.webautomator.internal.loader.ScriptLoader;
import tech.conexus.webautomator.internal.loader.ScriptLoaderImpl;
import tech.conexus.webautomator.script.Script;
import tech.conexus.webautomator.script.ScriptContext;
import tech.conexus.webautomator.shared.data.DataEntry;
import tech.conexus.webautomator.shared.data.DataStore;
import tech.conexus.webautomator.shared.data.config.ClassDataStore;
import tech.conexus.webautomator.shared.data.config.Configurable;
import tech.conexus.webautomator.shared.data.history.HistoryConfig;
import tech.conexus.webautomator.shared.data.library.ScriptLibrary;
import tech.conexus.webautomator.shared.data.library.xml.XMLScriptLibrary;
import tech.conexus.webautomator.shared.data.library.xml.XMLScriptManifest;
import tech.conexus.webautomator.shared.tabs.Tabs;

public class Bot implements ScriptExecutionListener, Configurable {
	public static final String JXBROWSER_DIRECTORY = System.getProperty("user.dir")+"/jxbrowser";
	public static final String JXBROWSER_CACHE_DIRECTORY = System.getProperty("user.dir")+"/jxbrowser-cache";
	
	static {
		//System.setProperty("JExplorer.runInIsolatedProcess", "false");
		LoggerProvider.setLevel(Level.SEVERE);
		
		BrowserPreferences.setUserAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9229");
		BrowserPreferences.setChromiumDir(JXBROWSER_DIRECTORY);
	}
	
	private JBotFrame botGUI;
	private ScriptLibrary scriptLibrary;
	private ScriptLoader scriptLoader;
	private ScriptExecutor scriptExecutor;
	
	private ClassDataStore mainConfig;
	
	private HistoryConfig historyConfig = new HistoryConfig();
	
	private Tabs userTabs;
	
	public Bot(String args[], boolean createGUI) {
		try {
			mainConfig = new ClassDataStore(new File("config/config.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		scriptLibrary = new XMLScriptLibrary("Local Script Library", "1.0", "Local script library that contains the scripts on the local machine.");
		
		XMLScriptManifest manifest = new XMLScriptManifest("bin", "tech.conexus.webautomator.scripts.googler.Googler", "3", "Googler", "1.0", "RAARR", "bin");
		scriptLibrary.addScript(manifest, true);
		
		XMLScriptManifest manifest2 = new XMLScriptManifest("bin", "tech.conexus.webautomator.scripts.blogscripts.AdClicker", "4", "Ad Clicker", "1.0", "RAARR", "bin");
		scriptLibrary.addScript(manifest2, true);
		
		XMLScriptManifest manifest3 = new XMLScriptManifest("bin", "tech.conexus.webautomator.scripts.tests.TestAdClicking", "5", "Test Script", "1.0", "RAARR", "bin");
		scriptLibrary.addScript(manifest3, true);
		
		XMLScriptManifest manifest4 = new XMLScriptManifest("bin", "tech.conexus.webautomator.scripts.internal.debug.TestElementSearch", "6", "Test Element Search", "1.0", "RAARR", "bin");
		scriptLibrary.addScript(manifest4, true);
		
		XMLScriptManifest manifest5 = new XMLScriptManifest("bin", "tech.conexus.webautomator.scripts.tests.TestAdChecking", "6", "Test Ad Checking", "1.0", "RAARR", "bin");
		scriptLibrary.addScript(manifest5, true);
		
		XMLScriptManifest manifest6 = new XMLScriptManifest("bin", "tech.conexus.webautomator.scripts.blogscripts.Shufflr", "7", "Shufflr", "1.0", "RAARR", "bin");
		scriptLibrary.addScript(manifest6, true);
		
		scriptLoader = new CachedScriptLoader(this, new ScriptContext(this));
		scriptLoader.addLibrary(scriptLibrary);
		
		scriptExecutor = new ScriptExecutor();
		scriptExecutor.addScriptExecutionListener(this);
		
		userTabs = new Tabs(null);
		
		if (createGUI) {
			botGUI = new JBotFrame(this);
			botGUI.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					try {
						mainConfig.save();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			});
		}

		try {
			//scriptExecutor.runScript(scriptLoader.loadScript(manifest));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Tabs getUserTabs() {
		return userTabs;
	}
	
	public DataStore getConfig() {
		return mainConfig;
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
	
	public HistoryConfig getHistoryConfig() {
		return historyConfig;
	}
	
	public JFrame getBotGUI() {
		return botGUI;
	}

	@Override
	public void onRunScript(Script script) {
		if (script instanceof Configurable) {
			mainConfig.addConfigurable((Configurable)script);
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

	@Override
	public void load(DataEntry configuration) {
	}

	@Override
	public void save(DataEntry configuration) {
	}
}
