package org.auriferous.bot.script.library;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.auriferous.bot.script.library.ScriptManifest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class LocalScriptManifest implements ScriptManifest {

	private String id;
	private String name;
	private String version;
	private String desc;
	private String path;
	
	private Document document;
	private Element scriptNode;
	
	public LocalScriptManifest(String src) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			document = builder.parse(new File(src));
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public LocalScriptManifest(InputSource source) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			document = builder.parse(source);
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		scriptNode = (Element)document.getElementsByTagName("script").item(0);

		this.id = XMLUtils.getElementAttr(scriptNode, "id");
		this.name = XMLUtils.getElementAttr(scriptNode, "name");
		this.version = XMLUtils.getElementAttr(scriptNode, "version");
		this.desc = XMLUtils.getElementAttr(scriptNode, "desc");
		this.path = XMLUtils.getElementAttr(scriptNode, "path");
	}
	
	public LocalScriptManifest(String id, String name, String version, String desc, String path) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.desc = desc;
		this.path = path;
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
	public String getDesc() {
		return desc;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getIconPath() {
		return null;
	}
}
