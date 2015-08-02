package org.auriferous.bot.script.library;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LocalScriptLibrary implements ScriptLibrary {
	private Document document;
	
	private Element libraryElement;
	private Element scriptsElement;
	private Element librariesElement;
	
	private String version;
	private String name;
	private String desc;

	public LocalScriptLibrary(String path) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			document = builder.parse(new File(path));
			
			libraryElement = (Element)document.getElementsByTagName("library").item(0);
			
			//this.name = XMLUtils.getElementAttr(document.getDocumentElement(), "name");
			//this.version = XMLUtils.getElementAttr(libraryElement, "version");
			//this.desc = XMLUtils.getElementAttr(libraryElement, "desc");
			
			scriptsElement = (Element)document.getElementsByTagName("scripts").item(0);
			librariesElement = (Element)document.getElementsByTagName("libraries").item(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public LocalScriptLibrary(String name, String version, String description) {
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
	public void addScript(ScriptManifest entry) {
		Element scriptElement = document.createElement("script");
		
		XMLUtils.appendAttrNode(document, scriptElement, "id", entry.getID());
		XMLUtils.appendAttrNode(document, scriptElement, "name", entry.getName());
		XMLUtils.appendAttrNode(document, scriptElement, "version", entry.getVersion());
		XMLUtils.appendAttrNode(document, scriptElement, "desc", entry.getDesc());
		XMLUtils.appendAttrNode(document, scriptElement, "path", entry.getPath());
		
		scriptsElement.appendChild(scriptElement);
	}

	@Override
	public ScriptManifest getScriptManifest(String selector) {
		NodeList nodes = document.getElementsByTagName("script");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Element scriptElement = (Element)nodes.item(i);
			
			String src = scriptElement.getAttribute("src");
			if (src.isEmpty()) {
				String id = scriptElement.getAttribute("id");
				
				if (id.equals(selector)) {
					String name = scriptElement.getAttribute("name");
					String version = scriptElement.getAttribute("version");
					String desc = scriptElement.getAttribute("desc");
					String path = scriptElement.getAttribute("path");
					
					return new LocalScriptManifest(id, name, version, desc, path);
				}
			} else {
				return new LocalScriptManifest(src);
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
				String id = scriptElement.getAttribute("id");
				String name = scriptElement.getAttribute("name");
				String version = scriptElement.getAttribute("version");
				String desc = scriptElement.getAttribute("desc");
				String path = scriptElement.getAttribute("path");
					
				manifests.add(new LocalScriptManifest(id, name, version, desc, path));
			} else {
				manifests.add(new LocalScriptManifest(src));
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
	public void addLibrary(ScriptLibrary library) {
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

	@Override
	public void addScript(String src) {
		Element scriptElement = document.createElement("script");
		
		scriptElement.setAttribute("src", src);
		
		scriptsElement.appendChild(scriptElement);
	}

	@Override
	public void addLibrary(String source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeLibrary(String source) {
		// TODO Auto-generated method stub
		
	}
}
