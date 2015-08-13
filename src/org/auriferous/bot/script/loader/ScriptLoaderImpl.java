package org.auriferous.bot.script.loader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.library.ScriptManifest;

public class ScriptLoaderImpl extends ScriptLoader{
	private ScriptContext context;

	public ScriptLoaderImpl(ScriptContext context) {
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
			
			return (Script)constr.newInstance(manifest, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
