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
package org.encog.ml.bayesian.query.sample;

import java.io.Serializable;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.query.BasicQuery;
import org.encog.util.Format;

/**
 * A sampling query allows probabilistic queries on a Bayesian network. Sampling
 * works by actually simulating the probabilities using a random number
 * generator. A sample size must be specified. The higher the sample size, the
 * more accurate the probability will be. However, the higher the sampling size,
 * the longer it takes to run the query.
 *
 * An enumeration query is more precise than the sampling query. However, the
 * enumeration query will become slow as the size of the Bayesian network grows.
 * Sampling can often be used for a quick estimation of a probability.
 */
public class SamplingQuery extends BasicQuery implements Serializable {

	/**
	 * The default sample size.
	 */
	public static final int DEFAULT_SAMPLE_SIZE = 100000;

	/**
	 * The sample size.
	 */
	private int sampleSize = DEFAULT_SAMPLE_SIZE;

	/**
	 * The number of usable samples. This is the set size for the average
	 * probability.
	 */
	private int usableSamples;

	/**
	 * The number of samples that matched the result the query is looking for.
	 */
	private int goodSamples;

	/**
	 * The total number of samples generated. This should match sampleSize at
	 * the end of a query.
	 */
	private int totalSamples;

	/**
	 * Construct a sampling query.
	 *
	 * @param theNetwork
	 *            The network that will be queried.
	 */
	public SamplingQuery(BayesianNetwork theNetwork) {
		super(theNetwork);
	}

	/**
	 * @return the sampleSize
	 */
	public int getSampleSize() {
		return this.sampleSize;
	}

	/**
	 * @param sampleSize
	 *            the sampleSize to set
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	/**
	 * Obtain the arguments for an event.
	 *
	 * @param event
	 *            The event.
	 * @return The arguments for that event, based on the other event values.
	 */
	private int[] obtainArgs(BayesianEvent event) {
		int[] result = new int[event.getParents().size()];

		int index = 0;
		for (BayesianEvent parentEvent : event.getParents()) {
			EventState state = this.getEventState(parentEvent);
			if (!state.isCalculated()) {
				return null;
			}
			result[index++] = state.getValue();

		}
		return result;
	}

	/**
	 * Set all events to random values, based on their probabilities.
	 *
	 * @param eventState
	 */
	private void randomizeEvents(EventState eventState) {
		// first, has this event already been randomized
		if (!eventState.isCalculated()) {
			// next, see if we can randomize the event passed
			int[] args = this.obtainArgs(eventState.getEvent());
			if (args != null) {
				eventState.randomize(args);
			}
		}

		// randomize children
		for (BayesianEvent childEvent : eventState.getEvent().getChildren()) {
			this.randomizeEvents(this.getEventState(childEvent));
		}
	}

	/**
	 * @return The number of events that are still uncalculated.
	 */
	private int countUnCalculated() {
		int result = 0;
		for (EventState state : this.getEvents().values()) {
			if (!state.isCalculated()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		this.locateEventTypes();
		this.usableSamples = 0;
		this.goodSamples = 0;
		this.totalSamples = 0;

		for (int i = 0; i < this.sampleSize; i++) {
			this.reset();

			int lastUncalculated = Integer.MAX_VALUE;
			int uncalculated;
			do {
				for (EventState state : this.getEvents().values()) {
					this.randomizeEvents(state);
				}
				uncalculated = this.countUnCalculated();
				if (uncalculated == lastUncalculated) {
					throw new BayesianError("Unable to calculate all nodes in the graph.");
				}
				lastUncalculated = uncalculated;
			} while (uncalculated > 0);

			// System.out.println("Sample:\n" + this.dumpCurrentState());
			this.totalSamples++;
			if (this.isNeededEvidence()) {
				this.usableSamples++;
				if (this.satisfiesDesiredOutcome()) {
					this.goodSamples++;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getProbability() {
		return (double) this.goodSamples / (double) this.usableSamples;
	}

	/**
	 * @return The current state as a string.
	 */
	public String dumpCurrentState() {
		StringBuilder result = new StringBuilder();
		for (EventState state : this.getEvents().values()) {
			result.append(state.toString());
			result.append("\n");
		}
		return result.toString();
	}

	@Override
	public SamplingQuery clone() {
		return new SamplingQuery(this.getNetwork());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[SamplingQuery: ");
		result.append(this.getProblem());
		result.append("=");
		result.append(Format.formatPercent(this.getProbability()));
		result.append(" ;good/usable=");
		result.append(Format.formatInteger(this.goodSamples));
		result.append("/");
		result.append(Format.formatInteger(this.usableSamples));
		result.append(";totalSamples=");
		result.append(Format.formatInteger(this.totalSamples));
		return result.toString();
	}

}
