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

package org.encog.ensemble.training;

import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;

public class ManhattanPropagationFactory implements EnsembleTrainFactory {

	private double learningRate = 0.01;
	private double dropoutRate = 0;

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	@Override
	public MLTrain getTraining(MLMethod mlMethod, MLDataSet trainingData) {
		return this.getTraining(mlMethod, trainingData, this.dropoutRate);

	}

	@Override
	public MLTrain getTraining(MLMethod mlMethod, MLDataSet trainingData, double dropoutRate) {
		ManhattanPropagation mp = new ManhattanPropagation((BasicNetwork) mlMethod, trainingData, 0.01);
		mp.setDroupoutRate(dropoutRate);
		return mp;
	}

	@Override
	public String getLabel() {
		String l = "manhattanprop" + this.learningRate;
		if (this.dropoutRate > 0) {
			l += "=" + this.dropoutRate;
		}
		return l;
	}

	@Override
	public void setDropoutRate(double rate) {
		this.dropoutRate = rate;
	}

}
