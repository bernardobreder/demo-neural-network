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
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.util.csv.CSVFormat;
import org.encog.util.logging.EncogLogging;

/**
 * The normalize command is used to normalize data. Data normalization generally
 * maps values from one number range to another, typically to -1 to 1.
 *
 */
public class CmdNormalize extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "NORMALIZE";

	/**
	 * Construct the normalize command.
	 *
	 * @param theAnalyst
	 *            The analyst to use.
	 */
	public CmdNormalize(final EncogAnalyst theAnalyst) {
		super(theAnalyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean executeCommand(final String args) {
		// get filenames
		final String sourceID = this.getProp().getPropertyString(ScriptProperties.NORMALIZE_CONFIG_SOURCE_FILE);
		final String targetID = this.getProp().getPropertyString(ScriptProperties.NORMALIZE_CONFIG_TARGET_FILE);

		final File sourceFile = this.getScript().resolveFilename(sourceID);
		final File targetFile = this.getScript().resolveFilename(targetID);

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning normalize");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "source file:" + sourceID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "target file:" + targetID);

		// mark generated
		this.getScript().markGenerated(targetID);

		// get formats
		final CSVFormat format = this.getScript().determineFormat();

		// prepare to normalize
		final AnalystNormalizeCSV norm = new AnalystNormalizeCSV();
		norm.setScript(this.getScript());
		this.getAnalyst().setCurrentQuantTask(norm);
		norm.setReport(new AnalystReportBridge(this.getAnalyst()));

		final boolean headers = this.getScript().expectInputHeaders(sourceID);
		norm.analyze(sourceFile, headers, format, this.getAnalyst());
		norm.setProduceOutputHeaders(true);
		norm.normalize(targetFile);
		this.getAnalyst().setCurrentQuantTask(null);
		return norm.shouldStop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return CmdNormalize.COMMAND_NAME;
	}

}
