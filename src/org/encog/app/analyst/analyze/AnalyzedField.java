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
package org.encog.app.analyst.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.Encog;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.util.csv.CSVFormat;

/**
 * This class represents a field that the Encog Analyst is in the process of
 * analyzing. This class is used to track statistical information on the field
 * that will help the Encog analyst determine what type of field this is, and
 * how to normalize it.
 *
 */
public class AnalyzedField extends DataField {

	/**
	 * Tge sum of all values of this field.
	 */
	private double total;

	/**
	 * The number of instances of this field.
	 */
	private int instances;

	/**
	 * The total for standard deviation calculation.
	 */
	private double devTotal;

	/**
	 * A mapping between the class names that the class items.
	 */
	private final Map<String, AnalystClassItem> classMap = new HashMap<>();

	/**
	 * The analyst script that the results are saved to.
	 */
	private final AnalystScript script;

	/**
	 * The numeric format.
	 */
	private CSVFormat fmt;

	/**
	 * Construct an analyzed field.
	 *
	 * @param theScript
	 *            The script being analyzed.
	 * @param name
	 *            The name of the field.
	 */
	public AnalyzedField(final AnalystScript theScript, final String name) {
		super(name);
		this.instances = 0;
		this.script = theScript;
		this.fmt = this.script.determineFormat();
	}

	/**
	 * Perform a pass one analysis of this field.
	 *
	 * @param v
	 *            The current value.
	 */
	public void analyze1(final String v) {

		boolean accountedFor = false;
		String str = v.trim();

		if (str.trim().length() == 0 || str.equals("?")) {
			this.setComplete(false);
			return;
		}

		this.instances++;

		if (this.isReal()) {
			if (this.fmt.isValid(str)) {
				final double d = this.fmt.parse(str);
				this.setMax(Math.max(d, this.getMax()));
				this.setMin(Math.min(d, this.getMin()));
				this.total += d;
				accountedFor = true;
			} else {
				this.setReal(false);
				if (!this.isInteger()) {
					this.setMax(0);
					this.setMin(0);
					this.setStandardDeviation(0);
				}
			}
		}

		if (this.isInteger()) {
			try {
				final int i = Integer.parseInt(str);
				this.setMax(Math.max(i, this.getMax()));
				this.setMin(Math.min(i, this.getMin()));
				if (!accountedFor) {
					this.total += i;
				}
			} catch (final NumberFormatException ex) {
				this.setInteger(false);
				if (!this.isReal()) {
					this.setMax(0);
					this.setMin(0);
					this.setStandardDeviation(0);
				}
			}
		}

		if (this.isClass()) {
			AnalystClassItem item;

			// is this a new class?
			if (!this.classMap.containsKey(str)) {
				item = new AnalystClassItem(str, str, 1);
				this.classMap.put(str, item);

				// do we have too many different classes?
				final int max = this.script.getProperties()
						.getPropertyInt(ScriptProperties.SETUP_CONFIG_MAX_CLASS_COUNT);
				if (this.classMap.size() > max) {
					this.setClass(false);
				}
			} else {
				item = this.classMap.get(str);
				item.increaseCount();
			}

		}
	}

	/**
	 * Perform a pass two analysis of this field.
	 *
	 * @param str
	 *            The current value.
	 */
	public void analyze2(final String str) {
		if (str.trim().length() == 0) {
			return;
		}

		if (this.isReal() || this.isInteger()) {
			if (!str.equals("") && !str.equals("?")) {
				final double d = this.fmt.parse(str);
				this.devTotal += Math.pow((d - this.getMean()), 2);
			}
		}
	}

	/**
	 * Complete pass 1.
	 */
	public void completePass1() {

		this.devTotal = 0;

		if (this.instances == 0) {
			this.setMean(0);
		} else {
			this.setMean(this.total / this.instances);
		}
	}

	/**
	 * Complete pass 2.
	 */
	public void completePass2() {
		this.setStandardDeviation(Math.sqrt(this.devTotal / this.instances));
	}

	/**
	 * Finalize the field, and create a DataField.
	 *
	 * @return The new DataField.
	 */
	public DataField finalizeField() {
		final DataField result = new DataField(this.getName());

		// if max and min are the same, we are dealing with a zero-sized range,
		// which will cause other issues. This is caused by ever number in the
		// column having exactly (or nearly exactly) the same value. Provide a
		// small range around that value so that every value in this column
		// normalizes
		// to the midpoint of the desired normalization range, typically 0 or
		// 0.5.
		if (Math.abs(this.getMax() - this.getMin()) < Encog.DEFAULT_DOUBLE_EQUAL) {
			result.setMin(this.getMin() - 0.0001);
			result.setMax(this.getMin() + 0.0001);
		} else {
			result.setMin(this.getMin());
			result.setMax(this.getMax());
		}

		result.setName(this.getName());
		result.setMean(this.getMean());
		result.setStandardDeviation(this.getStandardDeviation());
		result.setInteger(this.isInteger());
		result.setReal(this.isReal());
		result.setClass(this.isClass());
		result.setComplete(this.isComplete());
		result.setSource(this.getSource());

		result.getClassMembers().clear();

		if (result.isClass()) {
			final List<AnalystClassItem> list = this.getAnalyzedClassMembers();
			result.getClassMembers().addAll(list);
		}

		return result;
	}

	/**
	 * Get the class members.
	 *
	 * @return The class members.
	 */
	public List<AnalystClassItem> getAnalyzedClassMembers() {
		final List<String> sorted = new ArrayList<>();
		sorted.addAll(this.classMap.keySet());
		Collections.sort(sorted);

		final List<AnalystClassItem> result = new ArrayList<>();
		for (final String str : sorted) {
			result.add(this.classMap.get(str));
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(this.getClass().getSimpleName());
		result.append(" total=");
		result.append(this.total);
		result.append(", instances=");
		result.append(this.instances);
		result.append("]");
		return result.toString();
	}
}
