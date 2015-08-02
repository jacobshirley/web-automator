package org.auriferous.bot;

import org.auriferous.bot.script.library.LocalScriptManifest;
import org.auriferous.bot.script.loader.JarScriptLoader;
import org.auriferous.bot.script.library.LocalScriptLibrary;

public class Main {
    public static void main(String[] args) {
    	//Bot bot = new Bot(true);
    	
    	//LocalScriptLibrary library = new LocalScriptLibrary("output.xml");
    	
    	JarScriptLoader loader = new JarScriptLoader("file:C:/Users/Jacob/workspace/Ad Clicker/test.jar");
    	
    	loader.loadScript(null);
    }
}
