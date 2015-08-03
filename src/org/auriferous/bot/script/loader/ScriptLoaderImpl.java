package org.auriferous.bot.script.loader;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.script.library.xml.XMLScriptManifest;
import org.xml.sax.InputSource;

public class ScriptLoaderImpl extends ScriptLoader{
	private ScriptContext context;

	public ScriptLoaderImpl(ScriptContext context) {
		this.context = context;
	}

	@Override
	public Script loadScript(String selector) throws ClassNotFoundException {
		for (ScriptLibrary library : libraries) {
			
			ScriptManifest manifest = library.getScriptManifest(selector);
			if (manifest != null) {
				String libPath = library.getLibraryPath();
				String manPath = manifest.getManifestPath();
				File folder;
				if (!manPath.endsWith("/")) {
					folder = new File(manPath);
					if (!folder.isDirectory())
						folder = folder.getParentFile();
				} else
					folder = new File(manPath);
				
				try {
					URLClassLoader classLoader = new URLClassLoader(new URL[] {folder.toURI().toURL()});
					Class cls = classLoader.loadClass(manifest.getMainClass());
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}
}
