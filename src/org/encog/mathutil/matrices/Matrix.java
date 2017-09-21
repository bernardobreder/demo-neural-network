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
package org.encog.mathutil.matrices;

import java.io.Serializable;

import org.encog.Encog;
import org.encog.mathutil.matrices.decomposition.LUDecomposition;
import org.encog.mathutil.matrices.decomposition.QRDecomposition;
import org.encog.mathutil.randomize.RangeRandomizer;

/**
 * This class implements a mathematical matrix. Matrix math is very important to
 * neural network processing. Many of the neural network classes make use of the
 * matrix classes in this package.
 */
public class Matrix implements Cloneable, Serializable {

	/**
	 * Turn an array of doubles into a column matrix.
	 *
	 * @param input
	 *            A double array.
	 * @return A column matrix.
	 */
	public static Matrix createColumnMatrix(double[] input) {
		double[][] d = new double[input.length][1];
		for (int row = 0; row < d.length; row++) {
			d[row][0] = input[row];
		}
		return new Matrix(d);
	}

	/**
	 * Turn an array of doubles into a row matrix.
	 *
	 * @param input
	 *            A double array.
	 * @return A row matrix.
	 */
	public static Matrix createRowMatrix(double[] input) {
		double[][] d = new double[1][input.length];
		System.arraycopy(input, 0, d[0], 0, input.length);
		return new Matrix(d);
	}

	/**
	 * The matrix data.
	 */
	private double[][] matrix;

	/**
	 * Construct a bipolar matrix from an array of booleans.
	 *
	 * @param sourceMatrix
	 *            The booleans to create the matrix from.
	 */
	public Matrix(boolean[][] sourceMatrix) {
		this.matrix = new double[sourceMatrix.length][sourceMatrix[0].length];
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getCols(); c++) {
				if (sourceMatrix[r][c]) {
					this.set(r, c, 1);
				} else {
					this.set(r, c, -1);
				}
			}
		}
	}

	/**
	 * Create a matrix from an array of doubles.
	 *
	 * @param sourceMatrix
	 *            An array of doubles.
	 */
	public Matrix(double[][] sourceMatrix) {
		this.matrix = new double[sourceMatrix.length][sourceMatrix[0].length];
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getCols(); c++) {
				this.set(r, c, sourceMatrix[r][c]);
			}
		}
	}

	/**
	 * Create a blank array with the specified number of rows and columns.
	 *
	 * @param rows
	 *            How many rows in the matrix.
	 * @param cols
	 *            How many columns in the matrix.
	 */
	public Matrix(int rows, int cols) {
		this.matrix = new double[rows][cols];
	}

	/**
	 * Add a value to one cell in the matrix.
	 *
	 * @param row
	 *            The row to add to.
	 * @param col
	 *            The column to add to.
	 * @param value
	 *            The value to add to the matrix.
	 */
	public void add(int row, int col, double value) {
		this.validate(row, col);
		double newValue = this.matrix[row][col] + value;
		this.set(row, col, newValue);
	}

	/**
	 * Add the specified matrix to this matrix. This will modify the matrix to
	 * hold the result of the addition.
	 *
	 * @param theMatrix
	 *            The matrix to add.
	 */
	public void add(Matrix theMatrix) {
		double[][] source = theMatrix.getData();

		for (int row = 0; row < this.getRows(); row++) {
			for (int col = 0; col < this.getCols(); col++) {
				this.matrix[row][col] += source[row][col];
			}
		}
	}

	/**
	 * Set all rows and columns to zero.
	 */
	public void clear() {
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getCols(); c++) {
				this.matrix[r][c] = 0;
			}
		}
	}

	/**
	 * Create a copy of the matrix.
	 *
	 * @return A colne of the matrix.
	 */
	@Override
	public Matrix clone() {
		return new Matrix(this.matrix);
	}

	/**
	 * Compare to matrixes with the specified level of precision.
	 *
	 * @param theMatrix
	 *            The other matrix to compare to.
	 * @param precision
	 *            How much precision to use.
	 * @return True if the two matrixes are equal.
	 */
	public boolean equals(Matrix theMatrix, int precision) {

		if (precision < 0) {
			throw new MatrixError("Precision can't be a negative number.");
		}

		double test = Math.pow(10.0, precision);
		if (Double.isInfinite(test) || (test > Long.MAX_VALUE)) {
			throw new MatrixError("Precision of " + precision + " decimal places is not supported.");
		}

		int actualPrecision = (int) Math.pow(Encog.DEFAULT_PRECISION, precision);

		double[][] data = theMatrix.getData();

		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getCols(); c++) {
				if ((long) (this.matrix[r][c] * actualPrecision) != (long) (data[r][c] * actualPrecision)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Check to see if this matrix equals another, using default precision.
	 *
	 * @param other
	 *            The other matrix to compare.
	 * @return True if the two matrixes are equal.
	 */
	@Override
	public boolean equals(Object other) {

		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof Matrix)) {
			return false;
		}
		Matrix otherMyClass = (Matrix) other;

		return this.equals(otherMyClass, Encog.DEFAULT_PRECISION);
	}

	/**
	 * Create a matrix from a packed array.
	 *
	 * @param array
	 *            The packed array.
	 * @param index
	 *            Where to start in the packed array.
	 * @return The new index after this matrix has been read.
	 */
	public int fromPackedArray(double[] array, int index) {
		int i = index;
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getCols(); c++) {
				this.matrix[r][c] = array[i++];
			}
		}

		return i;
	}

	/**
	 * Read the specified cell in the matrix.
	 *
	 * @param row
	 *            The row to read.
	 * @param col
	 *            The column to read.
	 * @return The value at the specified row and column.
	 */
	public double get(int row, int col) {
		this.validate(row, col);
		return this.matrix[row][col];
	}

	/**
	 * @return A COPY of this matrix as a 2d array.
	 */
	public double[][] getArrayCopy() {
		double[][] result = new double[this.getRows()][this.getCols()];
		for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < this.getCols(); j++) {
				result[i][j] = this.matrix[i][j];
			}
		}
		return result;
	}

	/**
	 * Read one entire column from the matrix as a sub-matrix.
	 *
	 * @param col
	 *            The column to read.
	 * @return The column as a sub-matrix.
	 */
	public Matrix getCol(int col) {
		if (col > this.getCols()) {
			throw new MatrixError("Can't get column #" + col + " because it does not exist.");
		}

		double[][] newMatrix = new double[this.getRows()][1];

		for (int row = 0; row < this.getRows(); row++) {
			newMatrix[row][0] = this.matrix[row][col];
		}

		return new Matrix(newMatrix);
	}

	/**
	 * Get the columns in the matrix.
	 *
	 * @return The number of columns in the matrix.
	 */
	public int getCols() {
		return this.matrix[0].length;
	}

	/**
	 * @return Get the 2D matrix array.
	 */
	public double[][] getData() {
		return this.matrix;
	}

	/**
	 * Get a submatrix.
	 *
	 * @param i0
	 *            Initial row index.
	 * @param i1
	 *            row index.
	 * @param j0
	 *            Initial column index.
	 * @param j1
	 *            column index.
	 * @return The specified submatrix.
	 */
	public Matrix getMatrix(int i0, int i1, int j0, int j1) {

		Matrix result = new Matrix(i1 - i0 + 1, j1 - j0 + 1);
		double[][] b = result.getData();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					b[i - i0][j - j0] = this.matrix[i][j];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
		return result;
	}

	/**
	 * Get a submatrix.
	 *
	 * @param i0
	 *            Initial row index.
	 * @param i1
	 *            row index.
	 * @param c
	 *            Array of column indices.
	 * @return The specified submatrix.
	 */
	public Matrix getMatrix(int i0, int i1, int[] c) {
		Matrix result = new Matrix(i1 - i0 + 1, c.length);
		double[][] b = result.getData();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					b[i - i0][j] = this.matrix[i][c[j]];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
		return result;
	}

	/**
	 * Get a submatrix.
	 *
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            column index
	 * @return The specified submatrix.
	 */
	public Matrix getMatrix(int[] r, int j0, int j1) {
		Matrix result = new Matrix(r.length, j1 - j0 + 1);
		double[][] b = result.getData();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					b[i][j - j0] = this.matrix[r[i]][j];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return result;
	}

	/**
	 * Get a submatrix.
	 *
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @return The specified submatrix.
	 */
	public Matrix getMatrix(int[] r, int[] c) {
		Matrix result = new Matrix(r.length, c.length);
		double[][] b = result.getData();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					b[i][j] = this.matrix[r[i]][c[j]];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
		return result;
	}

	/**
	 * Get the specified row as a sub-matrix.
	 *
	 * @param row
	 *            The row to get.
	 * @return A matrix.
	 */
	public Matrix getRow(int row) {
		if (row > this.getRows()) {
			throw new MatrixError("Can't get row #" + row + " because it does not exist.");
		}

		double[][] newMatrix = new double[1][this.getCols()];

		for (int col = 0; col < this.getCols(); col++) {
			newMatrix[0][col] = this.matrix[row][col];
		}

		return new Matrix(newMatrix);
	}

	/**
	 * Get the number of rows in the matrix.
	 *
	 * @return The number of rows in the matrix.
	 */
	public int getRows() {
		return this.matrix.length;
	}

	/**
	 * Compute a hash code for this matrix.
	 *
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		long result = 0;
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getCols(); c++) {
				result += this.matrix[r][c];
			}
		}
		return (int) (result % Integer.MAX_VALUE);
	}

	/**
	 * @return The matrix inverted.
	 */
	public Matrix inverse() {
		return this.solve(MatrixMath.identity(this.getRows()));
	}

	/**
	 * Determine if the matrix is a vector. A vector is has either a single
	 * number of rows or columns.
	 *
	 * @return True if this matrix is a vector.
	 */
	public boolean isVector() {
		if (this.getRows() == 1) {
			return true;
		}
		return this.getCols() == 1;
	}

	/**
	 * Return true if every value in the matrix is zero.
	 *
	 * @return True if the matrix is all zeros.
	 */
	public boolean isZero() {
		for (int row = 0; row < this.getRows(); row++) {
			for (int col = 0; col < this.getCols(); col++) {
				if (this.matrix[row][col] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Multiply every value in the matrix by the specified value.
	 *
	 * @param value
	 *            The value to multiply the matrix by.
	 */
	public void multiply(double value) {

		for (int row = 0; row < this.getRows(); row++) {
			for (int col = 0; col < this.getCols(); col++) {
				this.matrix[row][col] *= value;
			}
		}
	}

	/**
	 * Multiply every row by the specified vector.
	 *
	 * @param vector
	 *            The vector to multiply by.
	 * @param result
	 *            The result to hold the values.
	 */
	public void multiply(double[] vector, double[] result) {
		for (int i = 0; i < this.getRows(); i++) {
			result[i] = 0;
			for (int j = 0; j < this.getCols(); j++) {
				result[i] += this.matrix[i][j] * vector[j];
			}
		}
	}

	/**
	 * Randomize the matrix.
	 *
	 * @param min
	 *            Minimum random value.
	 * @param max
	 *            Maximum random value.
	 */
	public void randomize(double min, double max) {
		for (int row = 0; row < this.getRows(); row++) {
			for (int col = 0; col < this.getCols(); col++) {
				this.matrix[row][col] = RangeRandomizer.randomize(min, max);
			}
		}

	}

	/**
	 * Set every value in the matrix to the specified value.
	 *
	 * @param value
	 *            The value to set the matrix to.
	 */
	public void set(double value) {
		for (int row = 0; row < this.getRows(); row++) {
			for (int col = 0; col < this.getCols(); col++) {
				this.matrix[row][col] = value;
			}
		}

	}

	/**
	 * Set an individual cell in the matrix to the specified value.
	 *
	 * @param row
	 *            The row to set.
	 * @param col
	 *            The column to set.
	 * @param value
	 *            The value to be set.
	 */
	public void set(int row, int col, double value) {
		this.validate(row, col);
		this.matrix[row][col] = value;
	}

	/**
	 * Set this matrix's values to that of another matrix.
	 *
	 * @param theMatrix
	 *            The other matrix.
	 */
	public void set(Matrix theMatrix) {
		double[][] source = theMatrix.getData();

		for (int row = 0; row < this.getRows(); row++) {
			for (int col = 0; col < this.getCols(); col++) {
				this.matrix[row][col] = source[row][col];
			}
		}
	}

	/**
	 * Set a submatrix.
	 *
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            column index
	 * @param x
	 *            A(i0:i1,j0:j1)
	 *
	 */
	public void setMatrix(int i0, int i1, int j0, int j1, Matrix x) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					this.matrix[i][j] = x.get(i - i0, j - j0);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 *
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            row index
	 * @param c
	 *            Array of column indices.
	 * @param x
	 *            The submatrix.
	 */

	public void setMatrix(int i0, int i1, int[] c, Matrix x) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					this.matrix[i][c[j]] = x.get(i - i0, j);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 *
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            column index
	 * @param x
	 *            A(r(:),j0:j1)
	 */

	public void setMatrix(int[] r, int j0, int j1, Matrix x) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					this.matrix[r[i]][j] = x.get(i, j - j0);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 *
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @param x
	 *            The matrix to set.
	 */
	public void setMatrix(int[] r, int[] c, Matrix x) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					this.matrix[r[i]][c[j]] = x.get(i, j);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
	}

	/**
	 * Get the size of the array. This is the number of elements it would take
	 * to store the matrix as a packed array.
	 *
	 * @return The size of the matrix.
	 */
	public int size() {
		return this.matrix[0].length * this.matrix.length;
	}

	/**
	 * Solve A*X = B.
	 *
	 * @param b
	 *            right hand side.
	 * @return Solution if A is square, least squares solution otherwise.
	 */
	public Matrix solve(Matrix b) {
		if (this.getRows() == this.getCols()) {
			return (new LUDecomposition(this)).solve(b);
		} else {
			return (new QRDecomposition(this)).solve(b);
		}
	}

	/**
	 * Sum all of the values in the matrix.
	 *
	 * @return The sum of the matrix.
	 */
	public double sum() {
		double result = 0;
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getCols(); c++) {
				result += this.matrix[r][c];
			}
		}
		return result;
	}

	/**
	 * Convert the matrix into a packed array.
	 *
	 * @return The matrix as a packed array.
	 */
	public double[] toPackedArray() {
		double[] result = new double[this.getRows() * this.getCols()];

		int index = 0;
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getCols(); c++) {
				result[index++] = this.matrix[r][c];
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[Matrix: rows=");
		result.append(this.getRows());
		result.append(",cols=");
		result.append(this.getCols());
		result.append("]");
		return result.toString();
	}

	/**
	 * Validate that the specified row and column are within the required
	 * ranges. Otherwise throw a MatrixError exception.
	 *
	 * @param row
	 *            The row to check.
	 * @param col
	 *            The column to check.
	 */
	private void validate(int row, int col) {
		if ((row >= this.getRows()) || (row < 0)) {
			String str = "The row:" + row + " is out of range:" + this.getRows();
			throw new MatrixError(str);
		}

		if ((col >= this.getCols()) || (col < 0)) {
			String str = "The col:" + col + " is out of range:" + this.getCols();
			throw new MatrixError(str);
		}
	}

	public boolean isSquare() {
		return this.getRows() == this.getCols();
	}

}