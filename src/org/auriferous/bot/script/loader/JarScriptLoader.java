package org.auriferous.bot.script.loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.library.LocalScriptManifest;
import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.ScriptManifest;
import org.xml.sax.InputSource;

public class JarScriptLoader extends ScriptLoader{
	private URLClassLoader classLoader = null;
	
	public JarScriptLoader(String path) {
		try {
			classLoader = new URLClassLoader(new URL[] {new URL(path)});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Script loadScript(String selector) {
		ScriptManifest manifest2 = new LocalScriptManifest(new InputSource(classLoader.getResourceAsStream("script.xml")));
		System.out.println(manifest2.getDesc());
		
		
		return null;
	}
}
