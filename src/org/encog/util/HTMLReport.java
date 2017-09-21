/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core

 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information on Heaton Research copyrights, licenses
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util;

public class HTMLReport {
	private StringBuilder text = new StringBuilder();

	public void beginHTML() {
		this.text.append("<html>");
	}

	public void endHTML() {
		this.text.append("</html>");
	}

	public void title(String str) {
		this.text.append("<head><title>");
		this.text.append(str);
		this.text.append("</title></head>");
	}

	public void beginPara() {
		this.text.append("<p>");
	}

	public void endPara() {
		this.text.append("</p>");
	}

	public void bold(String str) {
		this.text.append("<b>");
		this.text.append(encode(str));
		this.text.append("</b>");
	}

	public void para(String str) {
		this.text.append("<p>");
		this.text.append(encode(str));
		this.text.append("</p>");
	}

	public void clear() {
		this.text.setLength(0);
	}

	@Override
	public String toString() {
		return this.text.toString();
	}

	public void beginBody() {
		this.text.append("<body>");
	}

	public void endBody() {
		this.text.append("</body>");
	}

	public void h1(String title) {
		this.text.append("<h1>");
		this.text.append(encode(title));
		this.text.append("</h1>");
	}

	public void beginTable() {
		this.text.append("<table border=\"1\">");
	}

	public void endTable() {
		this.text.append("</table>");
	}

	public void beginRow() {
		this.text.append("<tr>");
	}

	public void endRow() {
		this.text.append("</tr>");
	}

	public void header(String head) {
		this.text.append("<th>");
		this.text.append(encode(head));
		this.text.append("</th>");
	}

	public void cell(String head) {
		this.cell(head, 0);
	}

	public void cell(String head, int colSpan) {
		this.text.append("<td");
		if (colSpan > 0) {
			this.text.append(" colspan=\"");
			this.text.append(colSpan);
			this.text.append("\"");
		}
		this.text.append(">");
		this.text.append(encode(head));
		this.text.append("</td>");
	}

	public void tablePair(String name, String value) {
		this.beginRow();
		this.text.append("<td><b>" + encode(name) + "</b></td>");
		this.cell(value);
		this.endRow();

	}

	public static String encode(String str) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);

			if (ch == '<') {
				result.append("&lt;");
			} else if (ch == '>') {
				result.append("&gt;");
			} else if (ch == '&') {
				result.append("&amp;");
			} else {
				result.append(ch);
			}

		}
		return result.toString();
	}

	public void h2(String title) {
		this.text.append("<h2>");
		this.text.append(encode(title));
		this.text.append("</h2>");
	}

	public void h3(String title) {
		this.text.append("<h3>");
		this.text.append(encode(title));
		this.text.append("</h3>");
	}

	public void beginList() {
		this.text.append("<ul>");
	}

	public void listItem(String str) {
		this.text.append("<li>");
		this.text.append(encode(str));

	}

	public void endList() {
		this.text.append("</ul>");
	}

	public void beginTableInCell(int colSpan) {
		this.text.append("<td");
		if (colSpan > 0) {
			this.text.append(" colspan=\"");
			this.text.append(colSpan);
			this.text.append("\"");
		}
		this.text.append(">");
		this.text.append("<table border=\"1\" width=\"100%\">");
	}

	public void endTableInCell() {
		this.text.append("</table></td>");

	}

	public void header(String head, int colSpan) {
		this.text.append("<th");
		if (colSpan > 0) {
			this.text.append(" colspan=\"");
			this.text.append(colSpan);
			this.text.append("\"");
		}
		this.text.append(">");
		this.text.append(encode(head));
		this.text.append("</td>");

	}
}
