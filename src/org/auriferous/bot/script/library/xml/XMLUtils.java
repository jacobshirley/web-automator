package org.auriferous.bot.script.library.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLUtils {
	public static final Element createAttrNode(Document document, String name, String value) {
		Element el = document.createElement(name);
		el.setTextContent(value);
		return el;
	}
	
	public static final void appendAttrNode(Document document, Element parent, String name, String value) {
		parent.appendChild(createAttrNode(document, name, value));
	}
	
	public static final String getElementAttr(Element parent, String name) {
		Node n = parent.getElementsByTagName(name).item(0);
		if (n == null)
			return null;
		
		return n.getTextContent();
	}
}
