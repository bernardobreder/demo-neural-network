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
package org.encog.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BagOfWords {
	private final Map<String, Integer> words = new HashMap<>();
	private boolean breakSpaces = true;
	private boolean ignoreCase = true;
	private int totalWords;
	private final int k;
	private int laplaceClasses;

	public BagOfWords(int laplace) {
		this.k = laplace;
	}

	public BagOfWords() {
		this(0);
	}

	public void process(String str) {
		if (this.breakSpaces) {
			this.processSpaces(str);
		} else {
			this.increase(str);
		}
	}

	private void processSpaces(String str) {
		StringBuilder word = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch != '\'' && !Character.isLetterOrDigit(ch)) {
				if (word.length() > 0) {
					this.increase(word.toString());
					word.setLength(0);
				}
			} else {
				word.append(ch);
			}
		}

		if (word.length() > 0) {
			this.increase(word.toString());
		}
	}

	public void increase(String word) {
		String word2;
		this.totalWords++;
		this.laplaceClasses++;

		if (this.ignoreCase) {
			word2 = word.toLowerCase();
		} else {
			word2 = word;
		}

		if (this.words.containsKey(word2)) {
			int i = this.words.get(word2);
			i++;
			this.words.put(word2, i);
		} else {
			this.words.put(word2, 1);
		}
	}

	/**
	 * @return the breakSpaces
	 */
	public boolean isBreakSpaces() {
		return this.breakSpaces;
	}

	/**
	 * @param breakSpaces
	 *            the breakSpaces to set
	 */
	public void setBreakSpaces(boolean breakSpaces) {
		this.breakSpaces = breakSpaces;
	}

	/**
	 * @return the ignoreCase
	 */
	public boolean isIgnoreCase() {
		return this.ignoreCase;
	}

	/**
	 * @param ignoreCase
	 *            the ignoreCase to set
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/**
	 * @return the words
	 */
	public Map<String, Integer> getWords() {
		return this.words;
	}

	public void clear() {
		this.words.clear();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		// sort
		Set<String> set = new TreeSet<>();
		set.addAll(this.words.keySet());

		// display
		for (String key : set) {
			int i = this.words.get(key);
			result.append(key);
			result.append(",");
			result.append(i);
			result.append("\n");
		}

		return result.toString();
	}

	public boolean contains(String word) {
		return this.words.containsKey(word);
	}

	public int getK() {
		return this.k;
	}

	/**
	 * @return the totalWords
	 */
	public int getTotalWords() {
		return this.totalWords;
	}

	public int getCount(String word) {
		String word2;
		if (this.ignoreCase) {
			word2 = word.toLowerCase();
		} else {
			word2 = word;
		}
		if (!this.words.containsKey(word2)) {
			return 0;
		}
		return this.words.get(word2);
	}

	public double probability(String word) {
		double n = ((double) this.getCount(word)) + ((double) this.k);
		double d = ((double) this.getTotalWords()) + (this.k * this.laplaceClasses);
		return n / d;
	}

	/**
	 * @return the laplaceClasses
	 */
	public int getLaplaceClasses() {
		return this.laplaceClasses;
	}

	/**
	 * @param laplaceClasses
	 *            the laplaceClasses to set
	 */
	public void setLaplaceClasses(int laplaceClasses) {
		this.laplaceClasses = laplaceClasses;
	}

	/**
	 * @param totalWords
	 *            the totalWords to set
	 */
	public void setTotalWords(int totalWords) {
		this.totalWords = totalWords;
	}

	public int getUniqueWords() {
		return this.words.size();
	}

}
