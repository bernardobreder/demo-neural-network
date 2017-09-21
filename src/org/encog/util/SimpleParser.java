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

public class SimpleParser {

	private String line;
	private int currentPosition;
	private int marked;

	public SimpleParser(String line) {
		this.line = line;
	}

	public int remaining() {
		return Math.max(this.line.length() - this.currentPosition, 0);
	}

	public boolean parseThroughComma() {
		this.eatWhiteSpace();
		if (!this.eol()) {
			if (this.peek() == ',') {
				this.advance();
				return true;
			}
		}

		return false;
	}

	public boolean isIdentifier() {
		if (this.eol()) {
			return false;
		}

		return Character.isLetterOrDigit(this.peek()) || this.peek() == '_';
	}

	public char peek() {
		if (this.eol()) {
			return (char) 0;
		} else if (this.currentPosition >= this.line.length()) {
			return (char) 0;
		} else {
			return this.line.charAt(this.currentPosition);
		}
	}

	public void advance() {
		if (this.currentPosition < this.line.length()) {
			this.currentPosition++;
		}
	}

	public boolean isWhiteSpace() {
		return " \t\n\r".indexOf(this.peek()) != -1;
	}

	public boolean eol() {
		return (this.currentPosition >= this.line.length());
	}

	public void eatWhiteSpace() {
		while (!this.eol() && this.isWhiteSpace()) {
			this.advance();
		}
	}

	public char readChar() {
		if (this.eol()) {
			return (char) 0;
		}

		char ch = this.peek();
		this.advance();
		return ch;
	}

	public String readToWhiteSpace() {
		StringBuilder result = new StringBuilder();

		while (!this.isWhiteSpace() && !this.eol()) {
			result.append(this.readChar());
		}

		return result.toString();
	}

	public boolean lookAhead(String str, boolean ignoreCase) {
		if (this.remaining() < str.length()) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			char c1 = str.charAt(i);
			char c2 = this.line.charAt(this.currentPosition + i);

			if (ignoreCase) {
				c1 = Character.toLowerCase(c1);
				c2 = Character.toLowerCase(c2);
			}

			if (c1 != c2) {
				return false;
			}
		}

		return true;
	}

	public void advance(int p) {
		this.currentPosition = Math.min(this.line.length(), this.currentPosition + p);
	}

	public void mark() {
		this.marked = this.currentPosition;
	}

	public void reset() {
		this.currentPosition = this.marked;
	}

	public String readQuotedString() {

		if (this.peek() != '\"') {
			return "";
		}

		StringBuilder result = new StringBuilder();

		this.advance();
		while (this.peek() != '\"' && !this.eol()) {
			result.append(this.readChar());
		}
		this.advance();

		return result.toString();
	}

	public String readToChars(String chs) {
		StringBuilder result = new StringBuilder();

		while (chs.indexOf(this.peek()) == -1 && !this.eol()) {
			result.append(this.readChar());
		}

		return result.toString();
	}

	public String getLine() {
		return this.line;
	}

	public boolean lookAhead(String c) {
		return this.lookAhead(c, false);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[Parser: ");
		result.append(this.line.substring(this.currentPosition));
		result.append("]");
		return result.toString();
	}
}
