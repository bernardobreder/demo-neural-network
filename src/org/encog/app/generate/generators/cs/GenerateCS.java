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
package org.encog.app.generate.generators.cs;

import java.io.File;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.app.generate.generators.AbstractGenerator;
import org.encog.app.generate.program.EncogGenProgram;
import org.encog.app.generate.program.EncogProgramNode;
import org.encog.app.generate.program.EncogTreeNode;
import org.encog.ml.MLFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.simple.EncogUtility;

public class GenerateCS extends AbstractGenerator {

	private boolean embed;

	private String useCSName(final String str) {
		String result = str.trim();
		if (Character.isLowerCase(str.charAt(0))) {
			result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
		}
		return result;
	}

	private void embedNetwork(final EncogProgramNode node) {
		this.addBreak();

		final File methodFile = (File) node.getArgs().get(0).getValue();

		final MLMethod method = (MLMethod) EncogDirectoryPersistence.loadObject(methodFile);

		if (!(method instanceof MLFactory)) {
			throw new EncogError("Code generation not yet supported for: " + method.getClass().getName());
		}

		final MLFactory factoryMethod = (MLFactory) method;

		final String methodName = factoryMethod.getFactoryType();
		final String methodArchitecture = factoryMethod.getFactoryArchitecture();

		// header
		this.addInclude("Encog.ML");
		this.addInclude("Encog.Persist");

		final StringBuilder line = new StringBuilder();
		line.append("public static IMLMethod ");
		line.append(this.useCSName(node.getName()));
		line.append("()");
		this.addLine(line.toString());
		this.indentLine("{");

		// create factory
		line.setLength(0);
		this.addInclude("Encog.ML.Factory");
		line.append("MLMethodFactory methodFactory = new MLMethodFactory();");
		this.addLine(line.toString());

		// factory create
		line.setLength(0);
		line.append("IMLMethod result = ");

		line.append("methodFactory.Create(");
		line.append("\"");
		line.append(methodName);
		line.append("\"");
		line.append(",");
		line.append("\"");
		line.append(methodArchitecture);
		line.append("\"");
		line.append(", 0, 0);");
		this.addLine(line.toString());

		line.setLength(0);
		this.addInclude("Encog.ML");
		line.append("((IMLEncodable)result).DecodeFromArray(WEIGHTS);");
		this.addLine(line.toString());

		// return
		this.addLine("return result;");

		this.unIndentLine("}");
	}

	private void embedTraining(final EncogProgramNode node) {

		final File dataFile = (File) node.getArgs().get(0).getValue();
		final MLDataSet data = EncogUtility.loadEGB2Memory(dataFile);
		this.addInclude("Encog.ML.Data.Basic");

		// generate the input data

		this.indentLine("public static readonly double[][] INPUT_DATA = {");
		for (final MLDataPair pair : data) {
			final MLData item = pair.getInput();

			final StringBuilder line = new StringBuilder();

			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "new double[] { ");
			line.append(" },");
			this.addLine(line.toString());
		}
		this.unIndentLine("};");

		this.addBreak();

		// generate the ideal data

		this.indentLine("public static readonly double[][] IDEAL_DATA = {");
		for (final MLDataPair pair : data) {
			final MLData item = pair.getIdeal();

			final StringBuilder line = new StringBuilder();

			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "new double[] { ");
			line.append(" },");
			this.addLine(line.toString());
		}
		this.unIndentLine("};");
	}

	@Override
	public void generate(final EncogGenProgram program, final boolean shouldEmbed) {
		this.embed = shouldEmbed;
		this.addLine("namespace EncogGenerated");
		this.indentLine("{");
		this.generateForChildren(program);
		this.generateImports(program);
		this.unIndentLine("}");
	}

	private void generateArrayInit(final EncogProgramNode node) {
		final StringBuilder line = new StringBuilder();
		line.append("public static readonly double[] ");
		line.append(node.getName());
		line.append(" = {");
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

		this.unIndentLine("};");
	}

	private void generateClass(final EncogProgramNode node) {
		this.addBreak();
		this.addLine("public class " + node.getName());
		this.indentLine("{");
		this.generateForChildren(node);
		this.unIndentLine("}");
	}

	private void generateComment(final EncogProgramNode commentNode) {
		this.addLine("// " + commentNode.getName());
	}

	private void generateConst(final EncogProgramNode node) {
		final StringBuilder line = new StringBuilder();
		line.append("public static readonly ");
		line.append(node.getArgs().get(1).getValue());
		line.append(" ");
		line.append(node.getName());
		line.append(" = \"");
		line.append(node.getArgs().get(0).getValue());
		line.append("\";");

		this.addLine(line.toString());
	}

	private void generateCreateNetwork(final EncogProgramNode node) {
		if (this.embed) {
			this.embedNetwork(node);
		} else {
			this.linkNetwork(node);
		}
	}

	private void generateEmbedTraining(final EncogProgramNode node) {
		if (this.embed) {
			this.embedTraining(node);
		}
	}

	private void generateForChildren(final EncogTreeNode parent) {
		for (final EncogProgramNode node : parent.getChildren()) {
			this.generateNode(node);
		}
	}

	private void generateFunction(final EncogProgramNode node) {
		this.addBreak();

		final StringBuilder line = new StringBuilder();
		line.append("public static void ");
		line.append(this.useCSName(node.getName()));
		line.append("() {");
		this.indentLine(line.toString());

		this.generateForChildren(node);
		this.unIndentLine("}");
	}

	private void generateFunctionCall(final EncogProgramNode node) {
		this.addBreak();
		final StringBuilder line = new StringBuilder();
		if (node.getArgs().get(0).getValue().toString().length() > 0) {
			String objType = node.getArgs().get(0).getValue().toString();

			if (objType.equals("MLMethod")) {
				objType = "IMLMethod";
			} else if (objType.equals("MLDataSet")) {
				objType = "IMLDataSet";
			}

			line.append(objType);
			line.append(" ");
			line.append(node.getArgs().get(1).getValue().toString());
			line.append(" = ");
		}

		line.append(this.useCSName(node.getName()));
		line.append("();");
		this.addLine(line.toString());
	}

	private void generateImports(final EncogGenProgram program) {
		final StringBuilder imports = new StringBuilder();
		for (final String str : this.getIncludes()) {
			imports.append("using ");
			imports.append(str);
			imports.append(";\n");
		}

		imports.append("\n");

		this.addToBeginning(imports.toString());

	}

	private void generateLoadTraining(final EncogProgramNode node) {
		this.addBreak();

		final File methodFile = (File) node.getArgs().get(0).getValue();

		final StringBuilder line = new StringBuilder();
		line.append("public static IMLDataSet CreateTraining() {");
		this.indentLine(line.toString());

		line.setLength(0);
		this.addInclude("Encog.ML.Data");

		if (this.embed) {
			line.append("IMLDataSet result = new BasicMLDataSet(INPUT_DATA,IDEAL_DATA);");
		} else {
			this.addInclude("Encog.Util.Simple");
			line.append("IMLDataSet result = EncogUtility.LoadEGB2Memory(new FileInfo(@\"");
			line.append(methodFile.getAbsolutePath());
			line.append("\"));");
		}

		this.addLine(line.toString());

		// return
		this.addLine("return result;");

		this.unIndentLine("}");
	}

	private void generateMainFunction(final EncogProgramNode node) {
		this.addBreak();
		this.addLine("static void Main(string[] args)");
		this.indentLine("{");
		this.generateForChildren(node);
		this.unIndentLine("}");
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
			this.generateCreateNetwork(node);
			break;
		case InitArray:
			this.generateArrayInit(node);
			break;
		case EmbedTraining:
			this.generateEmbedTraining(node);
			break;
		case LoadTraining:
			this.generateLoadTraining(node);
			break;
		}
	}

	private void linkNetwork(final EncogProgramNode node) {
		this.addBreak();

		final File methodFile = (File) node.getArgs().get(0).getValue();

		this.addInclude("Encog.ML");
		final StringBuilder line = new StringBuilder();
		line.append("public static IMLMethod ");
		line.append(node.getName());
		line.append("()");
		this.addLine(line.toString());
		this.indentLine("{");

		line.setLength(0);
		line.append("IMLMethod result = (IMLMethod)EncogDirectoryPersistence.LoadObject(new FileInfo(@\"");
		line.append(methodFile.getAbsolutePath());
		line.append("\"));");
		this.addLine(line.toString());

		// return
		this.addLine("return result;");

		this.unIndentLine("}");
	}
}
