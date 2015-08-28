package org.auriferous.bot.config.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.auriferous.bot.config.ConfigurableEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLConfigurableEntry<N,V> extends ConfigurableEntry<N,V>{
	private Element element;

	public XMLConfigurableEntry(Element el) {
		super((N)el.getTagName(), (V)el.getTextContent());
		this.element = el;
	}
	
	@Override
	public List<ConfigurableEntry<Object, Object>> get(Object path) {
		List<ConfigurableEntry<Object, Object>> l = new ArrayList<ConfigurableEntry<Object,Object>>();
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr;
		try {
			expr = xpath.compile(path.toString());
			NodeList nl = (NodeList) expr.evaluate(element, XPathConstants.NODESET);
			
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n instanceof Element) {
					l.add(getEntry((Element)n));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return l;
	}

	@Override
	public ConfigurableEntry<N, V> copy() {
		return new XMLConfigurableEntry<N, V>(element);
	}
	
	public Element getElement() {
		return element;
	}
	
	private XMLConfigurableEntry getEntry(ConfigurableEntry parent, Element element) {
		XMLConfigurableEntry xmlEntry = (XMLConfigurableEntry)parent;
		if (xmlEntry.getElement().equals(element)) 
			return xmlEntry;
		
		for (ConfigurableEntry entry : (List<ConfigurableEntry>)parent.getChildren()) {
			xmlEntry = getEntry(entry, element);
			if (xmlEntry != null) 
				return xmlEntry;
		}
		return null;
	}
	
	private XMLConfigurableEntry getEntry(Element element) {
		return getEntry(this, element);
	}
}
