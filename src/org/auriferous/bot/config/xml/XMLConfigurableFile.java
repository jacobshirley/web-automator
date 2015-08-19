package org.auriferous.bot.config.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	
	private ConfigurableEntry<Object,Object> getEntries(ConfigurableEntry<Object,Object> parent, NodeList nodes) {
		List<ConfigurableEntry<Object,Object>> list = new ArrayList<ConfigurableEntry<Object,Object>>();
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element elem = (Element)node;
				
				NodeList children = elem.getChildNodes();
				ConfigurableEntry<Object,Object> entry = new ConfigurableEntry<Object,Object>(elem.getTagName(), elem.getTextContent());
				
				getEntries(entry, children);
				
				list.add(entry);
			}
		}
		
		if (parent != null) {
			List<ConfigurableEntry> cChildren = parent.getChildren();
			if (cChildren != null)
				cChildren.addAll(list);
		}
		
		return parent;
	}
	
	@Override
	protected ConfigurableEntry<Object,Object> getEntries(Configurable configurable) {
		String className = configurable.getClass().getName();
		NodeList classElements = configElement.getElementsByTagName(className);
		if (classElements.getLength() > 0) {
			Element classElem = (Element)classElements.item(0);

			String root = classElem.getAttribute("root");
			if (root.length() > 0) {
				Element rootElem = (Element) classElem.getElementsByTagName(root).item(0);
				
				ConfigurableEntry<Object,Object> rootEntry = new ConfigurableEntry<Object, Object>(rootElem.getTagName(), rootElem.getTextContent());
				
				ConfigurableEntry<Object,Object> entries = getEntries(rootEntry, rootElem.getChildNodes());
				
				return entries;
			}
		}
		return null;
	}
	
	private void writeEntry(Element parent, ConfigurableEntry entry) {
		List<ConfigurableEntry<Object,Object>> entries = entry.getChildren();
		if (entries != null) {
			for (ConfigurableEntry<Object,Object> entry2 : entries) {
				Object val = entry2.getValue();
				Element elem = XMLUtils.createElement(document, ""+entry2.getKey(), ""+(val == null ? "" : val));

				writeEntry(elem, entry2);
				
				parent.appendChild(elem);
			}
		}
	}
	
	@Override
	public void compile() {
		for (Entry<String, Configurable> configurableEntry : configurables.entrySet()) {
			String cName = configurableEntry.getKey();
			Configurable configurable = configurableEntry.getValue();
			
			ConfigurableEntry<?,?> entries = configurable.getConfiguration();
			if (entries != null) {
				Element configurableElem = XMLUtils.createElement(document, cName, "");
				configurableElem.setAttribute("root", entries.getKey().toString());
				
				Element rootElem = XMLUtils.createElement(document, entries.getKey().toString(), "");
				
				writeEntry(rootElem, entries);
				
				configurableElem.appendChild(rootElem);
				
				Element tempElem = XMLUtils.getElement(configElement, cName);
				if (tempElem != null)
					configElement.removeChild(tempElem);
				
				configElement.appendChild(configurableElem);
			}
		}
	}
	
	public boolean save(File path) throws IOException {
		if (!path.exists()) {
			path.getParentFile().mkdirs();
			path.createNewFile();
		}
		
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
