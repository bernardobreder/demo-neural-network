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
package org.encog.ml.prg.train.rewrite;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.score.adjust.ComplexityAdjustedScore;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.PrgCODEC;
import org.encog.ml.prg.expvalue.DivisionByZeroError;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.opp.SubtreeCrossover;
import org.encog.ml.prg.opp.SubtreeMutation;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.ml.prg.train.ZeroEvalScoreFunction;
import org.encog.parse.expression.common.RenderCommonExpression;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestRewriteAlgebraic extends TestCase {

	public void eval(String start, String expect) {
		EncogProgramContext context = new EncogProgramContext();
		StandardExtensions.createNumericOperators(context);
		PrgPopulation pop = new PrgPopulation(context, 1);
		CalculateScore score = new ZeroEvalScoreFunction();

		TrainEA genetic = new TrainEA(pop, score);
		genetic.setValidationMode(true);
		genetic.setCODEC(new PrgCODEC());
		genetic.addOperation(0.95, new SubtreeCrossover());
		genetic.addOperation(0.05, new SubtreeMutation(context, 4));
		genetic.addScoreAdjuster(new ComplexityAdjustedScore());
		genetic.getRules().addRewriteRule(new RewriteConstants());
		genetic.getRules().addRewriteRule(new RewriteAlgebraic());

		EncogProgram expression = new EncogProgram(context);
		expression.compileExpression(start);
		RenderCommonExpression render = new RenderCommonExpression();
		genetic.getRules().rewrite(expression);
		Assert.assertEquals(expect, render.render(expression));
	}

	public static void checkMissingOperator(EncogProgramContext context) {

		PrgPopulation pop = new PrgPopulation(context, 1);
		CalculateScore score = new ZeroEvalScoreFunction();

		TrainEA genetic = new TrainEA(pop, score);
		genetic.setValidationMode(true);
		genetic.setCODEC(new PrgCODEC());
		genetic.addOperation(0.95, new SubtreeCrossover());
		genetic.addOperation(0.05, new SubtreeMutation(context, 4));
		genetic.addScoreAdjuster(new ComplexityAdjustedScore());
		genetic.getRules().addRewriteRule(new RewriteConstants());
		genetic.getRules().addRewriteRule(new RewriteAlgebraic());

		EncogProgram expression = new EncogProgram(context);
		expression.compileExpression("1");
		RenderCommonExpression render = new RenderCommonExpression();
		genetic.getRules().rewrite(expression);
		Assert.assertEquals("1", render.render(expression));
	}

	@Test
	public void testRequiredExtensions() {

		List<ProgramExtensionTemplate> required = new ArrayList<>();
		required.add(StandardExtensions.EXTENSION_VAR_SUPPORT);
		required.add(StandardExtensions.EXTENSION_CONST_SUPPORT);
		required.add(StandardExtensions.EXTENSION_NEG);
		required.add(StandardExtensions.EXTENSION_ADD);
		required.add(StandardExtensions.EXTENSION_SUB);
		required.add(StandardExtensions.EXTENSION_MUL);
		required.add(StandardExtensions.EXTENSION_PDIV);
		required.add(StandardExtensions.EXTENSION_POWER);

		// try to rewrite with each of the above missing
		for (ProgramExtensionTemplate missing : required) {
			// build a set, with the specified template missing
			EncogProgramContext context = new EncogProgramContext();

			FunctionFactory factory = context.getFunctions();

			for (ProgramExtensionTemplate temp : required) {
				if (temp != missing) {
					factory.addExtension(temp);
				}
			}

			// Should throw an exception
			try {
				TestRewriteAlgebraic.checkMissingOperator(context);
				Assert.fail("Did not throw error on missing: " + missing.toString());
			} catch (EncogError e) {
				// Should happen
			}

		}
	}

	public void testMinusZero() {
		this.eval("x-0", "x");
		this.eval("0-0", "0");
		this.eval("10-0", "10");
	}

	public void testZeroMul() {
		this.eval("0*0", "0");
		this.eval("1*0", "0");
		this.eval("0*1", "0");
	}

	public void testZeroDiv() {
		try {
			this.eval("0/0", "(0/0)");
			Assert.assertFalse(true);
		} catch (DivisionByZeroError ex) {
			// expected
		}
		this.eval("0/5", "0");
		this.eval("0/x", "0");
	}

	public void testZeroPlus() {
		this.eval("0+0", "0");
		this.eval("1+0", "1");
		this.eval("0+1", "1");
		this.eval("x+0", "x");
	}

	public void testPowerZero() {
		this.eval("0^x", "0");
		this.eval("0^0", "1");
		this.eval("x^0", "1");
		this.eval("1^0", "1");
		this.eval("-1^0", "1");
		this.eval("(x+y)^0", "1");
		this.eval("x+(x+y)^0", "(x+1)");
	}

	public void testOnePower() {
		this.eval("1^500", "1");
		this.eval("1^x", "1");
		this.eval("1^1", "1");
	}

	public void testDoubleNegative() {
		this.eval("--x", "x");
		// eval("-x","-(x)");
	}

	public void testMinusMinus() {
		this.eval("x--3", "(x+3)");
	}

	public void testPlusNeg() {
		this.eval("x+-y", "(x-y)");
		this.eval("x+-1", "(x-1)");
	}

	public void testVarOpVar() {
		this.eval("x-x", "0");
		this.eval("x+x", "(2*x)");
		this.eval("x*x", "(x^2)");
		this.eval("x/x", "1");
	}

	public void testMultiple() {
		this.eval("((x+-((0-(x+x))))*x)", "((x-(0-(2*x)))*x)");
	}

}
