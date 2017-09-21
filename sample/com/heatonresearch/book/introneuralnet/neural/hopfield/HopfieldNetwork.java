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
package com.heatonresearch.book.introneuralnet.neural.hopfield;

import com.heatonresearch.book.introneuralnet.neural.exception.NeuralNetworkError;
import com.heatonresearch.book.introneuralnet.neural.matrix.BiPolarUtil;
import com.heatonresearch.book.introneuralnet.neural.matrix.Matrix;
import com.heatonresearch.book.introneuralnet.neural.matrix.MatrixMath;

/**
 * HopfieldNetwork: This class implements a Hopfield neural network. A Hopfield
 * neural network is fully connected and consists of a single layer. Hopfield
 * neural networks are usually used for pattern recognition.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class HopfieldNetwork {

	/**
	 * The weight matrix for this neural network. A Hopfield neural network is a
	 * single layer, fully connected neural network.
	 *
	 * The inputs and outputs to/from a Hopfield neural network are always
	 * boolean values.
	 */
	private Matrix weightMatrix;

	public HopfieldNetwork(final int size) {
		this.weightMatrix = new Matrix(size, size);

	}

	/**
	 * Get the weight matrix for this neural network.
	 *
	 * @return
	 */
	public Matrix getMatrix() {
		return this.weightMatrix;
	}

	/**
	 * Get the size of this neural network.
	 *
	 * @return
	 */
	public int getSize() {
		return this.weightMatrix.getRows();
	}

	/**
	 * Present a pattern to the neural network and receive the result.
	 *
	 * @param pattern
	 *            The pattern to be presented to the neural network.
	 * @return The output from the neural network.
	 * @throws HopfieldException
	 *             The pattern caused a matrix math error.
	 */
	public boolean[] present(final boolean[] pattern) {

		final boolean output[] = new boolean[pattern.length];

		// convert the input pattern into a matrix with a single row.
		// also convert the boolean values to bipolar(-1=false, 1=true)
		final Matrix inputMatrix = Matrix.createRowMatrix(BiPolarUtil.bipolar2double(pattern));

		// Process each value in the pattern
		for (int col = 0; col < pattern.length; col++) {
			// The output for this input element is the dot product of the
			// input matrix and one column from the weight matrix.
			double dotProduct = MatrixMath.dotProduct(inputMatrix, MatrixMath.transpose(this.weightMatrix.getCol(col)));
			// Convert the dot product to either true or false.
			output[col] = dotProduct > 0;
		}

		return output;
	}

	/**
	 * Present a pattern to the neural network and receive the result.
	 *
	 * @param pattern
	 *            The pattern to be presented to the neural network.
	 * @return The output from the neural network.
	 * @throws HopfieldException
	 *             The pattern caused a matrix math error.
	 */
	public double weight(final boolean[] pattern) {
		double result = 0.0;
		final Matrix inputMatrix = Matrix.createRowMatrix(BiPolarUtil.bipolar2double(pattern));
		for (int col = 0; col < pattern.length; col++) {
			result += MatrixMath.dotProduct(inputMatrix, MatrixMath.transpose(this.weightMatrix.getCol(col)));
		}
		return result;
	}

	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 *
	 * @param pattern
	 *            The pattern to train on.
	 * @throws HopfieldException
	 *             The pattern size must match the size of this neural network.
	 */
	public void train(final boolean[] pattern) {
		if (pattern.length != this.weightMatrix.getRows()) {
			throw new NeuralNetworkError("Can't train a pattern of size " + pattern.length
					+ " on a hopfield network of size " + this.weightMatrix.getRows());
		}

		// Create a row matrix from the input, convert boolean to bipolar
		final Matrix m2 = Matrix.createRowMatrix(BiPolarUtil.bipolar2double(pattern));
		// Transpose the matrix and multiply by the original input matrix
		final Matrix m1 = MatrixMath.transpose(m2);
		final Matrix m3 = MatrixMath.multiply(m1, m2);

		// matrix 3 should be square by now, so create an identity
		// matrix of the same size.
		final Matrix identity = MatrixMath.identity(m3.getRows());

		// subtract the identity matrix
		final Matrix m4 = MatrixMath.subtract(m3, identity);

		// now add the calculated matrix, for this pattern, to the
		// existing weight matrix.
		this.weightMatrix = MatrixMath.add(this.weightMatrix, m4);

	}
}