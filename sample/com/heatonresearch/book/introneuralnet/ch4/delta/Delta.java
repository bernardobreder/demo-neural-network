/**
 * Introduction to Neural Networks with Java, 2nd Edition Copyright 2008 by
 * Heaton Research, Inc. http://www.heatonresearch.com/books/java-neural-2/
 *
 * ISBN13: 978-1-60439-008-7 ISBN: 1-60439-008-5
 *
 * This class is released under the: GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 */
package com.heatonresearch.book.introneuralnet.ch4.delta;

/**
 * Chapter 4: Machine Learning
 *
 * Delta: Learn, using the delta rule.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class Delta {

	/**
	 * Main method just instanciates a delta object and calls run.
	 *
	 * @param args
	 *            Not used
	 */
	public static void main(final String args[]) {
		Delta delta = new Delta();
		for (int i = 0; i < 100; i++) {
			System.out.println("***Beginning Epoch");
			delta.presentPattern(1, 1, 1, 5);
			delta.presentPattern(1, 0, 1, 2);
			delta.presentPattern(0, 1, 1, 3);
			delta.presentPattern(0, 0, 1, 1);
		}
		for (int i = 0; i < 10; i++) {
			System.out.println("***Beginning Epoch");
			delta.presentPattern(0, 0, 1, 10);
			delta.presentPattern(0, 1, 1, 5);
			delta.presentPattern(1, 0, 1, 1);
			delta.presentPattern(1, 1, 1, 1);
		}
	}

	/**
	 * Weight for neuron 1
	 */
	double w1;

	/**
	 * Weight for neuron 2
	 */
	double w2;

	/**
	 * Weight for neuron 3
	 */
	double w3;

	/**
	 * Learning rate
	 */
	double rate = 0.5;

	/**
	 * This method will calculate the error between the anticipated output and
	 * the actual output.
	 *
	 * @param actual
	 *            The actual output from the neural network.
	 * @param anticipated
	 *            The anticipated neuron output.
	 * @return The error.
	 */
	protected double getError(final double actual, final double anticipated) {
		return (anticipated - actual);
	}

	/**
	 * Present a pattern and learn from it.
	 *
	 * @param i1
	 *            Input to neuron 1
	 * @param i2
	 *            Input to neuron 2
	 * @param i3
	 *            Input to neuron 3
	 * @param anticipated
	 *            The anticipated output
	 */
	protected void presentPattern(final double i1, final double i2, final double i3, final double anticipated) {
		double error;
		double actual;
		double delta;

		// run the net as is on training data
		// and get the error
		System.out.print("Presented [" + i1 + "," + i2 + "," + i3 + "]");
		actual = this.recognize(i1, i2, i3);
		error = this.getError(actual, anticipated);
		System.out.print(" anticipated=" + anticipated);
		System.out.print(" actual=" + actual);
		System.out.println(" error=" + error);

		// adjust weight 1
		delta = this.trainingFunction(this.rate, i1, error);
		this.w1 += delta;

		// adjust weight 2
		delta = this.trainingFunction(this.rate, i2, error);
		this.w2 += delta;

		// adjust weight 3
		delta = this.trainingFunction(this.rate, i3, error);
		this.w3 += delta;
	}

	/**
	 * @param i1
	 *            Input to neuron 1
	 * @param i2
	 *            Input to neuron 2
	 * @param i3
	 *            Input to neuron 3
	 * @return the output from the neural network
	 */
	protected double recognize(final double i1, final double i2, final double i3) {
		final double a = (this.w1 * i1) + (this.w2 * i2) + (this.w3 * i3);
		return (a * .5);
	}

	/**
	 * The learningFunction implements the delta rule. This method will return
	 * the weight adjustment for the specified input neuron.
	 *
	 * @param rate
	 *            The learning rate
	 * @param input
	 *            The input neuron we're processing
	 * @param error
	 *            The error between the actual output and anticipated output.
	 * @return The amount to adjust the weight by.
	 */
	protected double trainingFunction(final double rate, final double input, final double error) {
		return rate * input * error;
	}
}