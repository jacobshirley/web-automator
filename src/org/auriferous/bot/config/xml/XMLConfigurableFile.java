package org.auriferous.bot.config.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.auriferous.bot.config.Configurable;
import org.auriferous.bot.config.ConfigurableEntry;
import org.auriferous.bot.config.ConfigurableFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLConfigurableFile extends ConfigurableFile{
	private Document document;
	private Element configElement;
	
	public XMLConfigurableFile(File file) throws IOException {
		super(file);
		
		if (!file.exists()) {
			init();
		} else {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
	
				document = builder.parse(this.file);
				
				configElement = (Element)document.getElementsByTagName("config").item(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public XMLConfigurableFile() {
		init();
	}
	
	private void init() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append("<?xml version=\"1.0\"?> <config> </config>");
			ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
			
			document = builder.parse(input);
			
			configElement = (Element)document.getElementsByTagName("config").item(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ConfigurableEntry[] getEntries(ConfigurableEntry parent, NodeList nodes) {
		List<ConfigurableEntry> list = new ArrayList<ConfigurableEntry>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element elem = (Element)node;
				
				NodeList children = elem.getChildNodes();
				ConfigurableEntry entry = new ConfigurableEntry(elem.getTagName(), elem.getTextContent());
				
				getEntries(entry, children);
				
				list.add(entry);
			}
		}
		if (!list.isEmpty()) {
			ConfigurableEntry[] entries = new ConfigurableEntry[list.size()];
			list.toArray(entries);
			return entries;
		}
		return null;
	}
	
	protected ConfigurableEntry[] getEntries(Configurable configurable) {
		String className = configurable.getClass().getName();
		NodeList list = configElement.getElementsByTagName(className);
		if (list.getLength() == 0) {
			return null;
		} else {
			return getEntries(null, list);
		}
	}
	
	private void writeEntry(Element parent, ConfigurableEntry entry) {
		Element entryElem = XMLUtils.createOrGetElement(document, parent, entry.getKey(), entry.getValue());
		
		ConfigurableEntry[] entries = entry.getChildren();
		if (entries != null) {
			for (ConfigurableEntry entry2 : entries) {
				Element elem = XMLUtils.createOrGetElement(document, entryElem, entry2.getKey(), entry2.getValue());

				writeEntry(elem, entry2);
				
				entryElem.appendChild(elem);
			}
		}
		
		parent.appendChild(entryElem);
	}
	
	public void compile() {
		for (Entry<String, Configurable> configurableEntry : configurables.entrySet()) {
			String cName = configurableEntry.getKey();
			Configurable configurable = configurableEntry.getValue();
			
			ConfigurableEntry[] entries = configurable.getConfigurableEntries();
			if (entries != null) {
				Element configurableElem = XMLUtils.createElement(document, cName, "");
				
				for (ConfigurableEntry entry : entries) {
					writeEntry(configurableElem, entry);
				}
				
				Element tempElem = XMLUtils.getElement(configElement, cName);
				if (tempElem != null)
					configElement.removeChild(tempElem);
				
				configElement.appendChild(configurableElem);
			}
		}
	}
	
	public boolean save() {
		return save(file);
	}
	
	public boolean save(File path) {
		compile();
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(path);
			Source input = new DOMSource(document);
	
			transformer.transform(input, output);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
