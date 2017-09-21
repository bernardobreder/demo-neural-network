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
package org.encog.app.generate.generators.ninja;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.generate.AnalystCodeGenerationError;
import org.encog.app.generate.generators.AbstractTemplateGenerator;
import org.encog.ml.MLMethod;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.EngineArray;
import org.encog.util.file.FileUtil;

public class GenerateNinjaScript extends AbstractTemplateGenerator {

	@Override
	public String getTemplatePath() {
		return "org/encog/data/ninja.cs";
	}

	private void addCols() {
		StringBuilder line = new StringBuilder();
		line.append("public readonly string[] ENCOG_COLS = {");

		boolean first = true;

		for (DataField df : this.getAnalyst().getScript().getFields()) {

			if (!df.getName().equalsIgnoreCase("time") && !df.getName().equalsIgnoreCase("prediction")) {
				if (!first) {
					line.append(",");
				}

				line.append("\"");
				line.append(df.getName());
				line.append("\"");
				first = false;
			}
		}

		line.append("};");
		this.addLine(line.toString());
	}

	private void processMainBlock() {
		EncogAnalyst analyst = this.getAnalyst();

		final String processID = analyst.getScript().getProperties()
				.getPropertyString(ScriptProperties.PROCESS_CONFIG_SOURCE_FILE);

		final String methodID = analyst.getScript().getProperties()
				.getPropertyString(ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final File methodFile = analyst.getScript().resolveFilename(methodID);

		final File processFile = analyst.getScript().resolveFilename(processID);

		MLMethod method = null;
		int[] contextTargetOffset = null;
		int[] contextTargetSize = null;
		boolean hasContext = false;
		int inputCount = 0;
		int[] layerContextCount = null;
		int[] layerCounts = null;
		int[] layerFeedCounts = null;
		int[] layerIndex = null;
		double[] layerOutput = null;
		double[] layerSums = null;
		int outputCount = 0;
		int[] weightIndex = null;
		double[] weights = null;
		;
		int[] activation = null;
		double[] p = null;

		if (methodFile.exists()) {
			method = (MLMethod) EncogDirectoryPersistence.loadObject(methodFile);
			FlatNetwork flat = ((BasicNetwork) method).getFlat();

			contextTargetOffset = flat.getContextTargetOffset();
			contextTargetSize = flat.getContextTargetSize();
			hasContext = flat.getHasContext();
			inputCount = flat.getInputCount();
			layerContextCount = flat.getLayerContextCount();
			layerCounts = flat.getLayerCounts();
			layerFeedCounts = flat.getLayerFeedCounts();
			layerIndex = flat.getLayerIndex();
			layerOutput = flat.getLayerOutput();
			layerSums = flat.getLayerSums();
			outputCount = flat.getOutputCount();
			weightIndex = flat.getWeightIndex();
			weights = flat.getWeights();
			activation = this.createActivations(flat);
			p = this.createParams(flat);
		}

		this.setIndentLevel(2);
		this.addLine("#region Encog Data");
		this.indentIn();
		this.addNameValue("public const string EXPORT_FILENAME", "\"" + FileUtil.toStringLiteral(processFile) + "\"");
		this.addCols();

		this.addNameValue("private readonly int[] _contextTargetOffset", contextTargetOffset);
		this.addNameValue("private readonly int[] _contextTargetSize", contextTargetSize);
		this.addNameValue("private const bool _hasContext", hasContext ? "true" : "false");
		this.addNameValue("private const int _inputCount", inputCount);
		this.addNameValue("private readonly int[] _layerContextCount", layerContextCount);
		this.addNameValue("private readonly int[] _layerCounts", layerCounts);
		this.addNameValue("private readonly int[] _layerFeedCounts", layerFeedCounts);
		this.addNameValue("private readonly int[] _layerIndex", layerIndex);
		this.addNameValue("private readonly double[] _layerOutput", layerOutput);
		this.addNameValue("private readonly double[] _layerSums", layerSums);
		this.addNameValue("private const int _outputCount", outputCount);
		this.addNameValue("private readonly int[] _weightIndex", weightIndex);
		this.addNameValue("private readonly double[] _weights", weights);
		this.addNameValue("private readonly int[] _activation", activation);
		this.addNameValue("private readonly double[] _p", p);
		this.indentOut();
		this.addLine("#endregion");
		this.setIndentLevel(0);
	}

	private void processCalc() {
		AnalystField firstOutputField = null;
		int barsNeeded = Math.abs(this.getAnalyst().determineMinTimeSlice());

		this.setIndentLevel(2);
		this.addLine("if( _inputCount>0 && CurrentBar>=" + barsNeeded + " )");
		this.addLine("{");
		this.indentIn();
		this.addLine("double[] input = new double[_inputCount];");
		this.addLine("double[] output = new double[_outputCount];");

		int idx = 0;
		for (AnalystField field : this.getAnalyst().getScript().getNormalize().getNormalizedFields()) {
			if (field.isInput()) {
				String str;
				DataField df = this.getAnalyst().getScript().findDataField(field.getName());

				switch (field.getAction()) {
				case PassThrough:
					str = EngineArray.replace(df.getSource(), "##", "" + (-field.getTimeSlice()));
					this.addLine("input[" + idx + "]=" + str + ";");
					idx++;
					break;
				case Normalize:
					str = EngineArray.replace(df.getSource(), "##", "" + (-field.getTimeSlice()));
					this.addLine("input[" + idx + "]=Norm(" + str + "," + field.getNormalizedHigh() + ","
							+ field.getNormalizedLow() + "," + field.getActualHigh() + "," + field.getActualLow()
							+ ");");
					idx++;
					break;
				case Ignore:
					break;
				default:
					throw new AnalystCodeGenerationError(
							"Can't generate Ninjascript code, unsupported normalizatoin action: "
									+ field.getAction().toString());
				}
			}
			if (field.isOutput()) {
				if (firstOutputField == null) {
					firstOutputField = field;
				}
			}
		}

		if (firstOutputField != null) {
			this.addLine("Compute(input,output);");
			this.addLine("Output.Set(DeNorm(output[0]" + "," + firstOutputField.getNormalizedHigh() + ","
					+ firstOutputField.getNormalizedLow() + "," + firstOutputField.getActualHigh() + ","
					+ firstOutputField.getActualLow() + "));");
			this.indentOut();
		}

		this.addLine("}");
		this.setIndentLevel(2);
	}

	private void processObtain() {
		this.setIndentLevel(3);
		this.addLine("double[] result = new double[ENCOG_COLS.Length];");

		int idx = 0;
		for (DataField df : this.getAnalyst().getScript().getFields()) {
			if (!df.getName().equalsIgnoreCase("time") && !df.getName().equalsIgnoreCase("prediction")) {
				String str = EngineArray.replace(df.getSource(), "##", "0");
				this.addLine("result[" + idx + "]=" + str + ";");
				idx++;
			}
		}
		this.addLine("return result;");
		this.setIndentLevel(0);
	}

	@Override
	public void processToken(String command) {
		if (command.equalsIgnoreCase("MAIN-BLOCK")) {
			this.processMainBlock();
		} else if (command.equals("CALC")) {
			this.processCalc();
		} else if (command.equals("OBTAIN")) {
			this.processObtain();
		}
		this.setIndentLevel(0);

	}

	@Override
	public String getNullArray() {
		return "null";
	}

}
