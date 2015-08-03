package org.auriferous.bot.script.library.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.auriferous.bot.script.library.LibraryRef;
import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.script.library.ScriptManifestRef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLScriptLibrary implements ScriptLibrary {
	private Document document;
	
	private Element libraryElement;
	private Element scriptsElement;
	private Element librariesElement;
	
	private String libraryPath;
	private String name;
	private String version;
	private String desc;
	
	private Map<ScriptManifest, String> manifests = new HashMap<ScriptManifest, String>();

	public XMLScriptLibrary(String path) {
		this.libraryPath = path;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			document = builder.parse(new File(path));
			
			libraryElement = (Element)document.getElementsByTagName("library").item(0);
			
			this.name = XMLUtils.getElementAttr(document.getDocumentElement(), "name");
			this.version = XMLUtils.getElementAttr(libraryElement, "version");
			this.desc = XMLUtils.getElementAttr(libraryElement, "desc");
			
			scriptsElement = (Element)document.getElementsByTagName("scripts").item(0);
			librariesElement = (Element)document.getElementsByTagName("libraries").item(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public XMLScriptLibrary(String name, String version, String description) {
		this.name = name;
		this.version = version;
		this.desc = description;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append("<?xml version=\"1.0\"?> <library> <scripts></scripts> <libraries></libraries> </library>");
			ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
			
			document = builder.parse(input);
			
			libraryElement = (Element)document.getElementsByTagName("library").item(0);
			scriptsElement = (Element)document.getElementsByTagName("scripts").item(0);
			librariesElement = (Element)document.getElementsByTagName("libraries").item(0);
			
			libraryElement.insertBefore(scriptsElement, XMLUtils.createAttrNode(document, "desc", name));
			libraryElement.insertBefore(scriptsElement, XMLUtils.createAttrNode(document, "version", name));
			libraryElement.insertBefore(scriptsElement, XMLUtils.createAttrNode(document, "name", name));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getLibraryPath() {
		return libraryPath;
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
	public void addScript(ScriptManifest entry, boolean merge) {
		Element scriptElement = document.createElement("script");
		
		scriptElement.setAttribute("src", entry.getManifestPath());
		
		if (merge) {
			XMLUtils.appendAttrNode(document, scriptElement, "mainClass", entry.getMainClass());
			XMLUtils.appendAttrNode(document, scriptElement, "id", entry.getID());
			XMLUtils.appendAttrNode(document, scriptElement, "name", entry.getName());
			XMLUtils.appendAttrNode(document, scriptElement, "version", entry.getVersion());
			XMLUtils.appendAttrNode(document, scriptElement, "desc", entry.getDesc());
			XMLUtils.appendAttrNode(document, scriptElement, "path", entry.getFilesPath());
		}
		scriptsElement.appendChild(scriptElement);
	}

	@Override
	public ScriptManifest getScriptManifest(String selector) {
		NodeList nodes = document.getElementsByTagName("script");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Element scriptElement = (Element)nodes.item(i);
			boolean empty = scriptElement.getChildNodes().getLength() == 0;
			String src = scriptElement.getAttribute("src");
			
			if (!empty) {
				String id = XMLUtils.getElementAttr(scriptElement, "id");
				
				if (id != null && id.equals(selector)) {
					//src = scriptElement.getAttribute("src");
					
					String mainClass = XMLUtils.getElementAttr(scriptElement, "mainClass");
					String name = XMLUtils.getElementAttr(scriptElement, "name");
					String version = XMLUtils.getElementAttr(scriptElement, "version");
					String desc = XMLUtils.getElementAttr(scriptElement, "desc");
					String path = XMLUtils.getElementAttr(scriptElement, "path");
					
					return new XMLScriptManifest(src, mainClass, id, name, version, desc, path);
				}
			} else {
				return new XMLScriptManifest(src);
			}
		}
		
		return null;
	}
	
	@Override
	public ScriptManifest[] getScripts() {
		List<ScriptManifest> manifests = new ArrayList<ScriptManifest>();
		
		NodeList nodes = document.getElementsByTagName("script");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Element scriptElement = (Element)nodes.item(i);
			String src = scriptElement.getAttribute("src");
			if (src.isEmpty()) {
				src = scriptElement.getAttribute("src");
				
				String mainClass = scriptElement.getAttribute("mainClass");
				String id = scriptElement.getAttribute("id");
				String name = scriptElement.getAttribute("name");
				String version = scriptElement.getAttribute("version");
				String desc = scriptElement.getAttribute("desc");
				String path = scriptElement.getAttribute("path");
					
				manifests.add(new XMLScriptManifest(src, mainClass, id, name, version, desc, path));
			} else {
				manifests.add(new XMLScriptManifest(src));
			}
		}
		ScriptManifest[] r = new ScriptManifest[manifests.size()];
		manifests.toArray(r);
		
		return r;
	}

	@Override
	public boolean hasScript(String selector) {
		return getScriptManifest(selector) != null;
	}

	@Override
	public void removeScript(String selector) {
	}

	@Override
	public void addLibrary(ScriptLibrary library, boolean merge) {
	}

	@Override
	public void removeLibrary(ScriptLibrary library) {
	}

	@Override
	public ScriptLibrary[] getLibraries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean save(File file) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(file);
			Source input = new DOMSource(document);
	
			transformer.transform(input, output);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
