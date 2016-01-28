package org.auriferous.webautomator.shared.data.library.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.auriferous.webautomator.shared.data.library.ScriptLibrary;
import org.auriferous.webautomator.shared.data.library.ScriptManifest;
import org.auriferous.webautomator.shared.data.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLScriptLibrary extends ScriptLibrary {
	private Document document;
	
	private Element libraryElement;
	private Element scriptsElement;
	private Element librariesElement;
	
	private String libraryPath;
	private String name;
	private String version;
	private String desc;

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
			
			libraryElement.insertBefore(XMLUtils.createElement(document, "name", this.name), scriptsElement);
			libraryElement.insertBefore(XMLUtils.createElement(document, "version", this.version), scriptsElement);
			libraryElement.insertBefore(XMLUtils.createElement(document, "desc", this.desc), scriptsElement);
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
	public void addScript(ScriptManifest manifest, boolean merge) {
		Element scriptElement = document.createElement("script");
		
		scriptElement.setAttribute("src", manifest.getManifestSrc());
		
		if (merge) {
			XMLUtils.appendElement(document, scriptElement, "mainClass", manifest.getMainClass());
			XMLUtils.appendElement(document, scriptElement, "id", manifest.getID());
			XMLUtils.appendElement(document, scriptElement, "name", manifest.getName());
			XMLUtils.appendElement(document, scriptElement, "version", manifest.getVersion());
			XMLUtils.appendElement(document, scriptElement, "desc", manifest.getDescription());
			XMLUtils.appendElement(document, scriptElement, "path", manifest.getFilesPath());
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
					return new XMLScriptManifest(scriptElement);
				}
			} else {
				return new XMLScriptManifest(src);
			}
		}
		
		return null;
	}
	
	@Override
	public ScriptManifest getScriptManifestAt(int index) {
		NodeList nodes = document.getElementsByTagName("script");
		
		Element scriptElement = (Element)nodes.item(index);
		ScriptManifest manifest = new XMLScriptManifest(scriptElement);
		manifest.setLibrary(this);
		return manifest;
	}
	
	@Override
	public ScriptManifest[] getScripts() {
		List<ScriptManifest> manifests = new ArrayList<ScriptManifest>();
		
		NodeList nodes = document.getElementsByTagName("script");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Element scriptElement = (Element)nodes.item(i);
			boolean empty = scriptElement.getChildNodes().getLength() == 0;
			String src = scriptElement.getAttribute("src");
			
			if (!empty) {
				ScriptManifest manifest = new XMLScriptManifest(scriptElement);
				manifest.setLibrary(this);
				
				manifests.add(manifest);
			} else {
				ScriptManifest manifest = new XMLScriptManifest(src);
				manifest.setLibrary(this);
				
				manifests.add(manifest);
			}
		}
		ScriptManifest[] results = new ScriptManifest[manifests.size()];
		manifests.toArray(results);
		return results;
	}

	@Override
	public boolean hasScript(String selector) {
		return getScriptManifest(selector) != null;
	}

	@Override
	public void removeScript(String selector) {
		NodeList nodes = document.getElementsByTagName("script");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Element scriptElement = (Element)nodes.item(i);
			
			boolean empty = scriptElement.getChildNodes().getLength() == 0;
			String src = scriptElement.getAttribute("src");
			
			if (!empty) {
				String id = XMLUtils.getElementAttr(scriptElement, "id");
				
				if (id != null && id.equals(selector)) {
					scriptElement.getParentNode().removeChild(scriptElement);
				}
			} else {
				if (src.equals(selector)) {
					scriptElement.getParentNode().removeChild(scriptElement);
				}
			}
		}
	}

	@Override
	public void addLibrary(ScriptLibrary library, boolean merge) {
		Element libraryElement = document.createElement("library");
		libraryElement.setAttribute("src", library.getLibraryPath());
		if (merge) {
			XMLUtils.appendElement(document, libraryElement, "name", library.getName());
			XMLUtils.appendElement(document, libraryElement, "version", library.getVersion());
			XMLUtils.appendElement(document, libraryElement, "description", library.getDescription());
			
			for (ScriptManifest manifest : library.getScripts()) {
				Element scriptElement = document.createElement("script");
				scriptElement.setAttribute("src", manifest.getManifestSrc());
				
				XMLUtils.appendElement(document, scriptElement, "mainClass", manifest.getMainClass());
				XMLUtils.appendElement(document, scriptElement, "id", manifest.getID());
				XMLUtils.appendElement(document, scriptElement, "name", manifest.getName());
				XMLUtils.appendElement(document, scriptElement, "version", manifest.getVersion());
				XMLUtils.appendElement(document, scriptElement, "desc", manifest.getDescription());
				XMLUtils.appendElement(document, scriptElement, "path", manifest.getFilesPath());
				
				libraryElement.appendChild(scriptElement);
			}
		}
		librariesElement.appendChild(libraryElement);
	}

	@Override
	public void removeLibrary(ScriptLibrary library) {
		
	}

	@Override
	public ScriptLibrary[] getLibraries() {
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
