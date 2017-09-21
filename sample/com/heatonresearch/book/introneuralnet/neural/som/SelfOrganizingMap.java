/**
 * Introduction to Neural Networks with Java, 2nd Edition
 * Copyright 2008 by Heaton Research, Inc.
 * http://www.heatonresearch.com/books/java-neural-2/
 *
 * ISBN13: 978-1-60439-008-7
 * ISBN:   1-60439-008-5
 *
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 */
package com.heatonresearch.book.introneuralnet.neural.som;

import java.io.Serializable;

import com.heatonresearch.book.introneuralnet.neural.matrix.Matrix;
import com.heatonresearch.book.introneuralnet.neural.matrix.MatrixMath;
import com.heatonresearch.book.introneuralnet.neural.som.NormalizeInput.NormalizationType;

/**
 * SelfOrganizingMap: The Self Organizing Map, or Kohonen Neural Network, is a
 * special type of neural network that is used to classify input into groups.
 * The SOM makes use of unsupervised training.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class SelfOrganizingMap implements Serializable {

	/**
	 * Do not allow patterns to go below this very small number.
	 */
	public static double VERYSMALL = 1.E-30;

	/**
	 * The weights of the output neurons base on the input from the input
	 * neurons.
	 */
	Matrix outputWeights;

	/**
	 * Output neuron activations
	 */
	protected double output[];

	/**
	 * Number of input neurons
	 */
	protected int inputNeuronCount;

	/**
	 * Number of output neurons
	 */
	protected int outputNeuronCount;

	/**
	 * The normalization type.
	 */
	protected NormalizationType normalizationType;

	/**
	 * The constructor.
	 *
	 * @param inputCount
	 *            Number of input neurons
	 * @param outputCount
	 *            Number of output neurons
	 * @param normalizationType
	 *            The normalization type to use.
	 */
	public SelfOrganizingMap(int inputCount, int outputCount, NormalizationType normalizationType) {
		this.inputNeuronCount = inputCount;
		this.outputNeuronCount = outputCount;
		this.outputWeights = new Matrix(this.outputNeuronCount, this.inputNeuronCount + 1);
		this.output = new double[this.outputNeuronCount];
		this.normalizationType = normalizationType;
	}

	/**
	 * Get the input neuron count.
	 *
	 * @return The input neuron count.
	 */
	public int getInputNeuronCount() {
		return this.inputNeuronCount;
	}

	/**
	 * Get the normalization type.
	 *
	 * @return The normalization type.
	 */
	public NormalizationType getNormalizationType() {
		return this.normalizationType;
	}

	/**
	 * Get the output neurons.
	 *
	 * @return The output neurons.
	 */
	public double[] getOutput() {
		return this.output;
	}

	/**
	 * Get the output neuron count.
	 *
	 * @return The output neuron count.
	 */
	public int getOutputNeuronCount() {
		return this.outputNeuronCount;
	}

	/**
	 * Get the output neuron weights.
	 *
	 * @return The output neuron weights.
	 */
	public Matrix getOutputWeights() {
		return this.outputWeights;
	}

	/**
	 * Set the output neuron weights.
	 *
	 * @param outputWeights
	 *            The new output neuron weights.
	 */
	public void setOutputWeights(Matrix outputWeights) {
		this.outputWeights = outputWeights;
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 *
	 * @param input
	 *            The input patter to present to the neural network.
	 * @return The winning neuron.
	 */
	public int winner(double input[]) {
		return this.winner(new NormalizeInput(input, this.normalizationType));
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 *
	 * @param input
	 *            The input pattern.
	 * @return The winning neuron.
	 */
	public int winner(NormalizeInput input) {
		int win = 0;
		Matrix matrix = input.getInputMatrix();
		double normfac = input.getNormfac();
		double biggest = Double.MIN_VALUE;
		for (int i = 0; i < this.outputNeuronCount; i++) {
			Matrix weight = this.outputWeights.getRow(i);
			this.output[i] = outputNormalize(MatrixMath.dotProduct(matrix, weight) * normfac);
			if (this.output[i] > biggest) {
				biggest = this.output[i];
				win = i;
			}
			this.output[i] = this.output[i] <= 0 ? 0 : 1;
		}

		return win;
	}

	public static double outputNormalize(double value) {
		return (value + 1.0) / 2.0;
	}

}