package org.auriferous.bot.script.library.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.auriferous.bot.script.library.ScriptManifest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class XMLScriptManifest implements ScriptManifest {
	private String manifestPath;
	
	private String id;
	private String name;
	private String version;
	private String desc;
	private String path;
	
	private Document document;

	private String mainClass;
	
	public XMLScriptManifest(String src) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			document = builder.parse(new File(src));
			init((Element)document.getElementsByTagName("script").item(0));
			
			this.manifestPath = src;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public XMLScriptManifest(String src, InputSource source) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			document = builder.parse(source);
			init((Element)document.getElementsByTagName("script").item(0));
			
			this.manifestPath = src;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init(Element element) {
		this.manifestPath = element.getAttribute("src");
		
		this.mainClass = XMLUtils.getElementAttr(element, "mainClass");
		this.id = XMLUtils.getElementAttr(element, "id");
		this.name = XMLUtils.getElementAttr(element, "name");
		this.version = XMLUtils.getElementAttr(element, "version");
		this.desc = XMLUtils.getElementAttr(element, "desc");
		this.path = XMLUtils.getElementAttr(element, "path");
	}
	
	public XMLScriptManifest(Element scriptElement) {
		init(scriptElement);
	}
	
	public XMLScriptManifest(String src, String mainClass, String id, String name, String version, String desc, String path) {
		this.manifestPath = src;
		
		this.mainClass = mainClass;
		this.id = id;
		this.name = name;
		this.version = version;
		this.desc = desc;
		this.path = path;
	}

	@Override
	public String getMainClass() {
		return mainClass;
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public String getFilesPath() {
		return path;
	}

	@Override
	public String getIconPath() {
		return null;
	}

	@Override
	public String getManifestPath() {
		return manifestPath;
	}
}
