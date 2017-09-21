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
import org.encog.app.analyst.csv.AnalystEvaluateCSV;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.ml.MLMethod;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.logging.EncogLogging;

/**
 * This class is used to evaluate a machine learning method. Evaluation data is
 * provided and the ideal and actual responses from the machine learning method
 * are written to a file.
 *
 */
public class CmdEvaluate extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "EVALUATE";

	/**
	 * Construct the evaluate command.
	 *
	 * @param theAnalyst
	 *            The analyst to use.
	 */
	public CmdEvaluate(final EncogAnalyst theAnalyst) {
		super(theAnalyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean executeCommand(final String args) {
		// get filenames
		final String evalID = this.getProp().getPropertyString(ScriptProperties.ML_CONFIG_EVAL_FILE);
		final String resourceID = this.getProp().getPropertyString(ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final String outputID = this.getProp().getPropertyString(ScriptProperties.ML_CONFIG_OUTPUT_FILE);

		final String query = this.getProp().getPropertyString(ScriptProperties.ML_CONFIG_QUERY);

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning evaluate");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "evaluate file:" + evalID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "resource file:" + resourceID);

		final File evalFile = this.getScript().resolveFilename(evalID);
		final File resourceFile = this.getScript().resolveFilename(resourceID);

		final File outputFile = this.getAnalyst().getScript().resolveFilename(outputID);

		final MLMethod method = (MLMethod) EncogDirectoryPersistence.loadObject(resourceFile);
		this.getAnalyst().setMethod(method);

		if (method instanceof BayesianNetwork) {
			((BayesianNetwork) method).defineClassificationStructure(query);
		}

		final boolean headers = true;

		final AnalystEvaluateCSV eval = new AnalystEvaluateCSV();
		eval.setScript(this.getScript());
		this.getAnalyst().setCurrentQuantTask(eval);
		eval.setReport(new AnalystReportBridge(this.getAnalyst()));
		eval.analyze(this.getAnalyst(), evalFile, headers,
				this.getProp().getPropertyCSVFormat(ScriptProperties.SETUP_CONFIG_CSV_FORMAT));
		eval.process(outputFile, method);
		this.getAnalyst().setCurrentQuantTask(null);
		return eval.shouldStop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return CmdEvaluate.COMMAND_NAME;
	}

}