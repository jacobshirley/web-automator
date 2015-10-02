package org.auriferous.bot.shared.data.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.auriferous.bot.shared.data.DataEntry;
import org.auriferous.bot.shared.data.DataStore;
import org.auriferous.bot.shared.data.RootEntry;
import org.auriferous.bot.shared.data.config.Configurable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLDataStore extends DataStore{
	private Document document;
	protected Element configElement;
	
	public XMLDataStore(String path) throws IOException {
		super(new RootEntry("config"));
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
			
			configElement = (Element)document.getFirstChild();
			setRootEntry(new RootEntry(configElement.getTagName()));
			
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public XMLDataStore(File file) throws IOException {
		super(file);
		
		if (!file.exists()) {
			init();
		} else {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				
				document = builder.parse(this.file);
				configElement = (Element)document.getFirstChild();
				
				setRootEntry(new RootEntry(configElement.getTagName()));
				load();
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}
	
	public XMLDataStore() {
		super(new RootEntry("config"));
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
			
			configElement = (Element)document.getFirstChild();
			setRootEntry(new RootEntry(configElement.getTagName()));
			
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void load() {
		load(rootEntry, configElement);
	}
	
	private void load(DataEntry parent, Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n instanceof Element) {
				Element e = (Element)n;

				DataEntry temp = new DataEntry(e.getTagName(), XMLUtils.getNodeValue(e));
				load(temp, e);
				
				parent.add(temp);
			}
		}
	}

	private void writeEntry(Element parent, DataEntry entry) {
		for (DataEntry entry2 : entry.getChildren()) {
			Object val = entry2.getValue();
			Element elem = XMLUtils.createElement(document, ""+entry2.getKey(), ""+(val == null ? "" : val));

			writeEntry(elem, entry2);
			
			parent.appendChild(elem);
		}
	}
	
	public void compile() {
		String val = rootEntry.getValue() == null ? "" : rootEntry.getValue().toString();
		Element base = XMLUtils.createElement(document, rootEntry.getKey().toString(), val);
		writeEntry(base, rootEntry);
		
		document.replaceChild(base, configElement);
	}
	
	@Override
	public boolean save(File path) throws IOException {
		if (!path.exists()) {
			if (path.getParentFile() != null)
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