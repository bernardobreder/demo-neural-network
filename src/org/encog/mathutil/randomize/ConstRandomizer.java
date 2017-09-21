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
package org.encog.mathutil.randomize;

/**
 * A randomizer that will create always set the random number to a const value,
 * used mainly for testing.
 *
 */
public class ConstRandomizer extends BasicRandomizer {

	/**
	 * The constant value.
	 */
	private final double value;

	/**
	 * Construct a range randomizer.
	 *
	 * @param value
	 *            The constant value.
	 */
	public ConstRandomizer(final double value) {
		this.value = value;
	}

	/**
	 * Generate a random number based on the range specified in the constructor.
	 *
	 * @param d
	 *            The range randomizer ignores this value.
	 * @return The random number.
	 */
	@Override
	public double randomize(final double d) {
		return this.value;
	}

}
