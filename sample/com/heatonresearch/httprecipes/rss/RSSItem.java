package com.heatonresearch.httprecipes.rss;

import java.util.Date;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Heaton Research Spider Copyright 2007 by Heaton Research, Inc.
 *
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 *
 * RSSItem: This is the class that holds individual RSS items, or stories, for
 * the RSS class.
 *
 * This class is released under the: GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 *
 * @author Jeff Heaton
 * @version 1.1
 */
public class RSSItem {

	/*
	 * The title of this item.
	 */
	private String title;

	/*
	 * The hyperlink to this item.
	 */
	private String link;

	/*
	 * The description of this item.
	 */
	private String description;

	/*
	 * The date this item was published.
	 */
	private Date date;

	/**
	 * Get the publication date.
	 *
	 * @return The publication date.
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Set the publication date.
	 *
	 * @param date
	 *            The new publication date.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Get the description.
	 *
	 * @return The description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get the description.
	 *
	 * @param description
	 *            The new description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the hyperlink.
	 *
	 * @return The hyperlink.
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * Set the hyperlink.
	 *
	 * @param link
	 *            The new hyperlink.
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Get the item title.
	 *
	 * @return The item title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Set the item title.
	 *
	 * @param title
	 *            The new item title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Load an item from the specified node.
	 *
	 * @param node
	 *            The Node to load the item from.
	 */
	public void load(Node node) {
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			String name = n.getNodeName();

			if (name.equalsIgnoreCase("title")) {
				this.title = RSS.getXMLText(n);
			} else if (name.equalsIgnoreCase("link")) {
				this.link = RSS.getXMLText(n);
			} else if (name.equalsIgnoreCase("description")) {
				this.description = RSS.getXMLText(n);
			} else if (name.equalsIgnoreCase("pubDate")) {
				String str = RSS.getXMLText(n);
				if (str != null) {
					this.date = RSS.parseDate(str);
				}
			}

		}
	}

	/**
	 * Convert the object to a String.
	 *
	 * @return The object as a String.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		builder.append("title=\"");
		builder.append(this.title);
		builder.append("\",link=\"");
		builder.append(this.link);
		builder.append("\",date=\"");
		builder.append(this.date);
		builder.append("\"]");
		return builder.toString();
	}
}
