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
package org.encog.ml.train.strategy.end;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.encog.ml.train.MLTrain;

public class EndMinutesStrategy implements EndTrainingStrategy {

	private final int minutes;
	private long startedTime;
	private boolean started;
	private final AtomicInteger minutesLeft = new AtomicInteger(0);

	public EndMinutesStrategy(int minutes) {
		this.minutes = minutes;
		this.started = false;
		this.minutesLeft.set(minutes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldStop() {
		final boolean timeUp = this.getMinutesLeft() <= 0;

		if (timeUp) {
			// LOG.info("Max training minutes exceed.");
		}

		return this.started && timeUp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(MLTrain train) {
		this.started = true;
		this.startedTime = System.currentTimeMillis();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postIteration() {
		final long now = System.currentTimeMillis();
		final long minutesPassed = (now - this.startedTime) / TimeUnit.MINUTES.toMillis(1);

		this.minutesLeft.set((int) Math.ceil(this.getMinutes() - minutesPassed));
		// LOG.info("Number of minutes remaining to termination by time: " +
		// this.minutesLeft.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preIteration() {
	}

	/**
	 * @return the minutesLeft
	 */
	public int getMinutesLeft() {
		return this.minutesLeft.get();
	}

	/**
	 * @return the minutes
	 */
	public int getMinutes() {
		return this.minutes;
	}

}
