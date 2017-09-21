package com.heatonresearch.httprecipes.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Heaton Research Spider Copyright 2007 by Heaton Research, Inc.
 *
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 *
 * RSS: This is the class that actually parses the RSS and builds a collection
 * of RSSItems. To make use of this class call the load method with a URL that
 * points to RSS.
 *
 * This class is released under the: GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 *
 * @author Jeff Heaton
 * @version 1.1
 */
public class RSS {
	/*
	 * All of the attributes for this RSS document.
	 */
	private Map<String, String> attributes = new HashMap<>();

	/*
	 * All RSS items, or stories, found.
	 */
	private List<RSSItem> items = new ArrayList<>();

	/**
	 * Simple utility function that converts a RSS formatted date into a Java
	 * date.
	 *
	 * @param datestr
	 *            The RSS formatted date.
	 * @return A Java java.util.date
	 */
	public static Date parseDate(String datestr) {
		try {
			DateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
			Date date = formatter.parse(datestr);
			return date;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Load the specified RSS item, or story.
	 *
	 * @param item
	 *            A XML node that contains a RSS item.
	 */
	private void loadItem(Node item) {
		RSSItem rssItem = new RSSItem();
		rssItem.load(item);
		this.items.add(rssItem);
	}

	/**
	 * Load the channle node.
	 *
	 * @param channel
	 *            A node that contains a channel.
	 */
	private void loadChannel(Node channel) {
		NodeList nl = channel.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodename = node.getNodeName();
			if (nodename.equalsIgnoreCase("item")) {
				this.loadItem(node);
			} else {
				if (node.getNodeType() != Node.TEXT_NODE) {
					this.attributes.put(nodename, RSS.getXMLText(node));
				}
			}
		}
	}

	/**
	 * Load all RSS data from the specified URL.
	 *
	 * @param url
	 *            URL that contains XML data.
	 * @throws IOException
	 *             Thrown if an IO error occurs.
	 * @throws SAXException
	 *             Thrown if there is an error while parsing XML.
	 * @throws ParserConfigurationException
	 *             Thrown if there is an XML parse config error.
	 */
	public void load(URL url) throws IOException, SAXException, ParserConfigurationException {
		URLConnection http = url.openConnection();
		InputStream is = http.getInputStream();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document d = factory.newDocumentBuilder().parse(is);

		Element e = d.getDocumentElement();
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodename = node.getNodeName();

			// RSS 2.0
			if (nodename.equalsIgnoreCase("channel")) {
				this.loadChannel(node);
			}
			// RSS 1.0
			else if (nodename.equalsIgnoreCase("item")) {
				this.loadItem(node);
			}
		}

	}

	/**
	 * Simple utility method that obtains the text of an XML node.
	 *
	 * @param n
	 *            The XML node.
	 * @return The text of the specified XML node.
	 */
	public static String getXMLText(Node n) {
		NodeList list = n.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n2 = list.item(i);
			if (n2.getNodeType() == Node.TEXT_NODE) {
				return n2.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Get the list of attributes.
	 *
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	/**
	 * Convert the object to a String.
	 *
	 * @return The object as a String.
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		Set<String> set = this.attributes.keySet();
		for (String item : set) {
			str.append(item);
			str.append('=');
			str.append(this.attributes.get(item));
			str.append('\n');
		}
		str.append("Items:\n");
		for (RSSItem item : this.items) {
			str.append(item.toString());
			str.append('\n');
		}
		return str.toString();
	}

}
