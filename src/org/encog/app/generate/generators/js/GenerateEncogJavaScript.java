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
package org.encog.app.generate.generators.js;

import java.io.File;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.app.generate.AnalystCodeGenerationError;
import org.encog.app.generate.generators.AbstractGenerator;
import org.encog.app.generate.program.EncogGenProgram;
import org.encog.app.generate.program.EncogProgramNode;
import org.encog.app.generate.program.EncogTreeNode;
import org.encog.engine.network.activation.ActivationElliott;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.simple.EncogUtility;

public class GenerateEncogJavaScript extends AbstractGenerator {

	private void embedNetwork(final EncogProgramNode node) {
		this.addBreak();

		final File methodFile = (File) node.getArgs().get(0).getValue();

		final MLMethod method = (MLMethod) EncogDirectoryPersistence.loadObject(methodFile);

		if (!(method instanceof MLFactory)) {
			throw new EncogError("Code generation not yet supported for: " + method.getClass().getName());
		}

		final FlatNetwork flat = ((ContainsFlat) method).getFlat();

		// header
		final StringBuilder line = new StringBuilder();
		line.append("public static MLMethod ");
		line.append(node.getName());
		line.append("() {");
		this.indentLine(line.toString());

		// create factory
		line.setLength(0);

		this.addLine("var network = ENCOG.BasicNetwork.create( null );");
		this.addLine("network.inputCount = " + flat.getInputCount() + ";");
		this.addLine("network.outputCount = " + flat.getOutputCount() + ";");
		this.addLine("network.layerCounts = " + this.toSingleLineArray(flat.getLayerCounts()) + ";");
		this.addLine("network.layerContextCount = " + this.toSingleLineArray(flat.getLayerContextCount()) + ";");
		this.addLine("network.weightIndex = " + this.toSingleLineArray(flat.getWeightIndex()) + ";");
		this.addLine("network.layerIndex = " + this.toSingleLineArray(flat.getLayerIndex()) + ";");
		this.addLine("network.activationFunctions = " + this.toSingleLineArray(flat.getActivationFunctions()) + ";");
		this.addLine("network.layerFeedCounts = " + this.toSingleLineArray(flat.getLayerFeedCounts()) + ";");
		this.addLine("network.contextTargetOffset = " + this.toSingleLineArray(flat.getContextTargetOffset()) + ";");
		this.addLine("network.contextTargetSize = " + this.toSingleLineArray(flat.getContextTargetSize()) + ";");
		this.addLine("network.biasActivation = " + this.toSingleLineArray(flat.getBiasActivation()) + ";");
		this.addLine("network.beginTraining = " + flat.getBeginTraining() + ";");
		this.addLine("network.endTraining=" + flat.getEndTraining() + ";");
		this.addLine("network.weights = WEIGHTS;");
		this.addLine("network.layerOutput = " + this.toSingleLineArray(flat.getLayerOutput()) + ";");
		this.addLine("network.layerSums = " + this.toSingleLineArray(flat.getLayerSums()) + ";");

		// return
		this.addLine("return network;");

		this.unIndentLine("}");
	}

	private void embedTraining(final EncogProgramNode node) {

		final File dataFile = (File) node.getArgs().get(0).getValue();
		final MLDataSet data = EncogUtility.loadEGB2Memory(dataFile);

		// generate the input data

		this.indentLine("var INPUT_DATA = [");
		for (final MLDataPair pair : data) {
			final MLData item = pair.getInput();

			final StringBuilder line = new StringBuilder();

			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "[ ");
			line.append(" ],");
			this.addLine(line.toString());
		}
		this.unIndentLine("];");

		this.addBreak();

		// generate the ideal data

		this.indentLine("var IDEAL_DATA = [");
		for (final MLDataPair pair : data) {
			final MLData item = pair.getIdeal();

			final StringBuilder line = new StringBuilder();

			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "[ ");
			line.append(" ],");
			this.addLine(line.toString());
		}
		this.unIndentLine("];");
	}

	@Override
	public void generate(final EncogGenProgram program, final boolean shouldEmbed) {
		if (!shouldEmbed) {
			throw new AnalystCodeGenerationError("Must embed when generating Javascript");
		}
		this.generateForChildren(program);
	}

	private void generateArrayInit(final EncogProgramNode node) {
		final StringBuilder line = new StringBuilder();
		line.append("var ");
		line.append(node.getName());
		line.append(" = [");
		this.indentLine(line.toString());

		final double[] a = (double[]) node.getArgs().get(0).getValue();

		line.setLength(0);

		int lineCount = 0;
		for (int i = 0; i < a.length; i++) {
			line.append(CSVFormat.EG_FORMAT.format(a[i], Encog.DEFAULT_PRECISION));
			if (i < (a.length - 1)) {
				line.append(",");
			}

			lineCount++;
			if (lineCount >= 10) {
				this.addLine(line.toString());
				line.setLength(0);
				lineCount = 0;
			}
		}

		if (line.length() > 0) {
			this.addLine(line.toString());
			line.setLength(0);
		}

		this.unIndentLine("];");
	}

	private void generateClass(final EncogProgramNode node) {
		this.addBreak();

		this.addLine("<!DOCTYPE html>");
		this.addLine("<html>");
		this.addLine("<head>");
		this.addLine("<title>Encog Generated Javascript</title>");
		this.addLine("</head>");
		this.addLine("<body>");
		this.addLine("<script src=\"../encog.js\"></script>");
		this.addLine("<script src=\"../encog-widget.js\"></script>");
		this.addLine("<pre>");
		this.addLine("<script type=\"text/javascript\">");

		this.generateForChildren(node);

		this.addLine("</script>");
		this.addLine(
				"<noscript>Your browser does not support JavaScript! Note: if you are trying to view this in Encog Workbench, right-click file and choose \"Open as Text\".</noscript>");
		this.addLine("</pre>");
		this.addLine("</body>");
		this.addLine("</html>");
	}

	private void generateComment(final EncogProgramNode commentNode) {
		this.addLine("// " + commentNode.getName());
	}

	private void generateConst(final EncogProgramNode node) {
		final StringBuilder line = new StringBuilder();
		line.append("var ");
		line.append(node.getName());
		line.append(" = \"");
		line.append(node.getArgs().get(0).getValue());
		line.append("\";");

		this.addLine(line.toString());
	}

	private void generateForChildren(final EncogTreeNode parent) {
		for (final EncogProgramNode node : parent.getChildren()) {
			this.generateNode(node);
		}
	}

	private void generateFunction(final EncogProgramNode node) {
		this.addBreak();

		final StringBuilder line = new StringBuilder();
		line.append("function ");
		line.append(node.getName());
		line.append("() {");
		this.indentLine(line.toString());

		this.generateForChildren(node);
		this.unIndentLine("}");
	}

	private void generateFunctionCall(final EncogProgramNode node) {
		this.addBreak();
		final StringBuilder line = new StringBuilder();
		if (node.getArgs().get(0).getValue().toString().length() > 0) {
			line.append("var ");
			line.append(node.getArgs().get(1).getValue().toString());
			line.append(" = ");
		}

		line.append(node.getName());
		line.append("();");
		this.addLine(line.toString());
	}

	private void generateMainFunction(final EncogProgramNode node) {
		this.addBreak();
		this.generateForChildren(node);
	}

	private void generateNode(final EncogProgramNode node) {
		switch (node.getType()) {
		case Comment:
			this.generateComment(node);
			break;
		case Class:
			this.generateClass(node);
			break;
		case MainFunction:
			this.generateMainFunction(node);
			break;
		case Const:
			this.generateConst(node);
			break;
		case StaticFunction:
			this.generateFunction(node);
			break;
		case FunctionCall:
			this.generateFunctionCall(node);
			break;
		case CreateNetwork:
			this.embedNetwork(node);
			break;
		case InitArray:
			this.generateArrayInit(node);
			break;
		case EmbedTraining:
			this.embedTraining(node);
			break;
		}
	}

	private String toSingleLineArray(final ActivationFunction[] activationFunctions) {
		final StringBuilder result = new StringBuilder();
		result.append('[');
		for (int i = 0; i < activationFunctions.length; i++) {
			if (i > 0) {
				result.append(',');
			}

			final ActivationFunction af = activationFunctions[i];
			if (af instanceof ActivationSigmoid) {
				result.append("ENCOG.ActivationSigmoid.create()");
			} else if (af instanceof ActivationTANH) {
				result.append("ENCOG.ActivationTANH.create()");
			} else if (af instanceof ActivationLinear) {
				result.append("ENCOG.ActivationLinear.create()");
			} else if (af instanceof ActivationElliott) {
				result.append("ENCOG.ActivationElliott.create()");
			} else {
				throw new AnalystCodeGenerationError(
						"Unsupported activatoin function for code generation: " + af.getClass().getSimpleName());
			}

		}
		result.append(']');
		return result.toString();
	}

	private String toSingleLineArray(final double[] d) {
		final StringBuilder line = new StringBuilder();
		line.append("[");
		for (int i = 0; i < d.length; i++) {
			line.append(CSVFormat.EG_FORMAT.format(d[i], Encog.DEFAULT_PRECISION));
			if (i < (d.length - 1)) {
				line.append(",");
			}
		}
		line.append("]");
		return line.toString();
	}

	private String toSingleLineArray(final int[] d) {
		final StringBuilder line = new StringBuilder();
		line.append("[");
		for (int i = 0; i < d.length; i++) {
			line.append(d[i]);
			if (i < (d.length - 1)) {
				line.append(",");
			}
		}
		line.append("]");
		return line.toString();
	}
}
