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
package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.logging.EncogLogging;
import org.encog.util.validate.ValidateNetwork;

/**
 * This command is used to perform training on a machine learning method and
 * dataset.
 *
 */
public class CmdTrain extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "TRAIN";

	/**
	 * Construct the train command.
	 *
	 * @param analyst
	 *            The analyst to use.
	 */
	public CmdTrain(final EncogAnalyst analyst) {
		super(analyst);
	}

	/**
	 * Create a trainer, use cross validation if enabled.
	 *
	 * @param method
	 *            The method to use.
	 * @param trainingSet
	 *            The training set to use.
	 * @return The trainer.
	 */
	private MLTrain createTrainer(final MLMethod method, final MLDataSet trainingSet) {

		final MLTrainFactory factory = new MLTrainFactory();

		final String type = this.getProp().getPropertyString(ScriptProperties.ML_TRAIN_TYPE);
		final String args = this.getProp().getPropertyString(ScriptProperties.ML_TRAIN_ARGUMENTS);

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "training type:" + type);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "training args:" + args);

		if (method instanceof MLResettable) {
			this.getAnalyst().setMethod(method);
		}

		MLTrain train = factory.create(method, trainingSet, type, args);

		if (this.getKfold() > 0) {
			train = new CrossValidationKFold(train, this.getKfold());
		}

		return train;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean executeCommand(final String args) {

		this.setKfold(this.obtainCross());
		final MLDataSet trainingSet = this.obtainTrainingSet();
		MLMethod method = this.obtainMethod();
		final MLTrain trainer = this.createTrainer(method, trainingSet);

		if (method instanceof BayesianNetwork) {
			final String query = this.getProp().getPropertyString(ScriptProperties.ML_CONFIG_QUERY);
			((BayesianNetwork) method).defineClassificationStructure(query);
		}

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning training");

		this.performTraining(trainer, method, trainingSet);

		final String resourceID = this.getProp().getPropertyString(ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);
		final File resourceFile = this.getAnalyst().getScript().resolveFilename(resourceID);

		// reload the method
		method = null;

		if (trainer instanceof EvolutionaryAlgorithm) {
			EvolutionaryAlgorithm ea = (EvolutionaryAlgorithm) trainer;
			method = ea.getPopulation();
		}

		if (method == null) {
			method = trainer.getMethod();
		}

		EncogDirectoryPersistence.saveObject(resourceFile, method);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "save to:" + resourceID);
		trainingSet.close();

		return this.getAnalyst().shouldStopCommand();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return CmdTrain.COMMAND_NAME;
	}

	/**
	 * Perform the training.
	 *
	 * @param train
	 *            The training method.
	 * @param method
	 *            The ML method.
	 * @param trainingSet
	 *            The training set.
	 */
	private void performTraining(final MLTrain train, final MLMethod method, final MLDataSet trainingSet) {

		ValidateNetwork.validateMethodToData(method, trainingSet);
		final double targetError = this.getProp().getPropertyDouble(ScriptProperties.ML_TRAIN_TARGET_ERROR);
		this.getAnalyst().reportTrainingBegin();
		final int maxIteration = this.getAnalyst().getMaxIteration();

		if (train.getImplementationType() == TrainingImplementationType.OnePass) {
			train.iteration();
			this.getAnalyst().reportTraining(train);
		} else {
			do {
				train.iteration();
				this.getAnalyst().reportTraining(train);
			} while ((train.getError() > targetError) && !this.getAnalyst().shouldStopCommand()
					&& !train.isTrainingDone() && ((maxIteration == -1) || (train.getIteration() < maxIteration)));
		}
		train.finishTraining();

		this.getAnalyst().reportTrainingEnd();
		this.getAnalyst().setMethod(train.getMethod());
	}

}
