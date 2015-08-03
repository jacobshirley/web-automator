package org.auriferous.bot.script.loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
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
				return loadScript(manifest);
			}
		}
		
		return null;
	}

	@Override
	public Script loadScript(ScriptManifest manifest) throws ClassNotFoundException {
		String manPath = manifest.getManifestPath();
		System.out.println(manPath);
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
			
			Constructor constr = cls.getConstructor(ScriptContext.class);
			constr.setAccessible(true);
			
			return (Script)constr.newInstance(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
