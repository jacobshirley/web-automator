package org.auriferous.bot.script.loader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

import org.auriferous.bot.Bot;
import org.auriferous.bot.data.library.ScriptManifest;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;

public class ScriptLoaderImpl extends ScriptLoader{
	private ScriptContext context;
	private Bot bot;

	public ScriptLoaderImpl(Bot bot, ScriptContext context) {
		this.bot = bot;
		this.context = context;
	}

	@Override
	public Script loadScript(ScriptManifest manifest) throws ClassNotFoundException {
		String manPath = manifest.getManifestSrc();
		
		File folder;
		if (manPath != null && !manPath.equals("")) {
			if (!manPath.endsWith("/")) {
				folder = new File(manPath);
				if (!folder.isDirectory())
					folder = folder.getParentFile();
			} else
				folder = new File(manPath);
			
			folder = new File(folder, manifest.getFilesPath());
		} else {
			folder = new File(manifest.getFilesPath());
		}
		
		try {
			URLClassLoader classLoader = new URLClassLoader(new URL[] {folder.toURI().toURL()});
			Class<?> cls = classLoader.loadClass(manifest.getMainClass());
			
			Constructor<?> constr = cls.getConstructor(ScriptManifest.class, ScriptContext.class);
			constr.setAccessible(true);
			
			Script script = (Script)constr.newInstance(manifest, context);
			
			classLoader.close();
			
			return script;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
