package org.auriferous.bot;

import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.script.library.xml.XMLScriptLibrary;
import org.auriferous.bot.script.library.xml.XMLScriptManifest;
import org.auriferous.bot.script.loader.ScriptLoaderImpl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
    	//Bot bot = new Bot(true);
    	
    	//LocalScriptLibrary library = new LocalScriptLibrary("output.xml");
    	
    	ScriptLoaderImpl loader;
		try {
			File file = new File("C:/Users/Jacob/Desktop/JarBuilder-0.8.0/build/"); 
			
			ScriptLibrary library = new XMLScriptLibrary("output.xml");
			ScriptManifest newScript = new XMLScriptManifest("C:/Users/Jacob/Desktop/JarBuilder-0.8.0/build", "ch.fulgur.jarbuilder.JarBuilder", "what", "", "", "", "");
			
			library.addScript(newScript, true);

			library.save(new File("output.xml"));
            //convert the file to URL format
			URL url = file.toURI().toURL(); 
			
			loader = new ScriptLoaderImpl(null);
			loader.addLibrary(library);
			
			loader.loadScript("what");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    	
    }
}
