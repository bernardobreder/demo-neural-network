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
package org.encog.mathutil.matrices.decomposition;

/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 *
 * Copyright 2008-2010 by Heaton Research Inc.
 *
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 *
 * http://www.heatonresearch.com/copyright.html
 */
import org.encog.mathutil.EncogMath;
import org.encog.mathutil.matrices.Matrix;

/**
 * Singular Value Decomposition.
 * <P>
 * For an m-by-n matrix A with m ≥ n, the singular value decomposition is an
 * m-by-n orthogonal matrix U, an n-by-n diagonal matrix S, and an n-by-n
 * orthogonal matrix V so that A = U*S*V'.
 * <P>
 * The singular values, sigma[k] = S[k][k], are ordered so that sigma[0] ≥
 * sigma[1] ≥ ... ≥ sigma[n-1].
 * <P>
 * The singular value decompostion always exists, so the constructor will never
 * fail. The matrix condition number and the effective numerical rank can be
 * computed from this decomposition.
 *
 * This file based on a class from the public domain JAMA package.
 * http://math.nist.gov/javanumerics/jama/
 */
public class SingularValueDecomposition {

	/**
	 * Arrays for internal storage of U and V.
	 */
	private double[][] U, V;

	/**
	 * Array for internal storage of singular values.
	 */
	private double[] s;

	/**
	 * Row and column dimensions.
	 */
	private int m, n;

	/**
	 * Construct the singular value decomposition Structure to access U, S and
	 * V.
	 *
	 * @param Arg
	 *            Rectangular matrix
	 */
	public SingularValueDecomposition(Matrix Arg) {

		// Derived from LINPACK code.
		// Initialize.
		double[][] A = Arg.getArrayCopy();
		this.m = Arg.getRows();
		this.n = Arg.getCols();

		/*
		 * Apparently the failing cases are only a proper subset of (m<n), so
		 * let's not throw error. Correct fix to come later? if (m<n) { throw
		 * new IllegalArgumentException("Jama SVD only works for m >= n"); }
		 */
		int nu = Math.min(this.m, this.n);
		this.s = new double[Math.min(this.m + 1, this.n)];
		this.U = new double[this.m][nu];
		this.V = new double[this.n][this.n];
		double[] e = new double[this.n];
		double[] work = new double[this.m];
		boolean wantu = true;
		boolean wantv = true;

		// Reduce A to bidiagonal form, storing the diagonal elements
		// in s and the super-diagonal elements in e.

		int nct = Math.min(this.m - 1, this.n);
		int nrt = Math.max(0, Math.min(this.n - 2, this.m));
		for (int k = 0; k < Math.max(nct, nrt); k++) {
			if (k < nct) {

				// Compute the transformation for the k-th column and
				// place the k-th diagonal in s[k].
				// Compute 2-norm of k-th column without under/overflow.
				this.s[k] = 0;
				for (int i = k; i < this.m; i++) {
					this.s[k] = EncogMath.hypot(this.s[k], A[i][k]);
				}
				if (this.s[k] != 0.0) {
					if (A[k][k] < 0.0) {
						this.s[k] = -this.s[k];
					}
					for (int i = k; i < this.m; i++) {
						A[i][k] /= this.s[k];
					}
					A[k][k] += 1.0;
				}
				this.s[k] = -this.s[k];
			}
			for (int j = k + 1; j < this.n; j++) {
				if ((k < nct) & (this.s[k] != 0.0)) {

					// Apply the transformation.

					double t = 0;
					for (int i = k; i < this.m; i++) {
						t += A[i][k] * A[i][j];
					}
					t = -t / A[k][k];
					for (int i = k; i < this.m; i++) {
						A[i][j] += t * A[i][k];
					}
				}

				// Place the k-th row of A into e for the
				// subsequent calculation of the row transformation.

				e[j] = A[k][j];
			}
			if (wantu & (k < nct)) {

				// Place the transformation in U for subsequent back
				// multiplication.

				for (int i = k; i < this.m; i++) {
					this.U[i][k] = A[i][k];
				}
			}
			if (k < nrt) {

				// Compute the k-th row transformation and place the
				// k-th super-diagonal in e[k].
				// Compute 2-norm without under/overflow.
				e[k] = 0;
				for (int i = k + 1; i < this.n; i++) {
					e[k] = EncogMath.hypot(e[k], e[i]);
				}
				if (e[k] != 0.0) {
					if (e[k + 1] < 0.0) {
						e[k] = -e[k];
					}
					for (int i = k + 1; i < this.n; i++) {
						e[i] /= e[k];
					}
					e[k + 1] += 1.0;
				}
				e[k] = -e[k];
				if ((k + 1 < this.m) & (e[k] != 0.0)) {

					// Apply the transformation.

					for (int i = k + 1; i < this.m; i++) {
						work[i] = 0.0;
					}
					for (int j = k + 1; j < this.n; j++) {
						for (int i = k + 1; i < this.m; i++) {
							work[i] += e[j] * A[i][j];
						}
					}
					for (int j = k + 1; j < this.n; j++) {
						double t = -e[j] / e[k + 1];
						for (int i = k + 1; i < this.m; i++) {
							A[i][j] += t * work[i];
						}
					}
				}
				if (wantv) {

					// Place the transformation in V for subsequent
					// back multiplication.

					for (int i = k + 1; i < this.n; i++) {
						this.V[i][k] = e[i];
					}
				}
			}
		}

		// Set up the final bidiagonal matrix or order p.

		int p = Math.min(this.n, this.m + 1);
		if (nct < this.n) {
			this.s[nct] = A[nct][nct];
		}
		if (this.m < p) {
			this.s[p - 1] = 0.0;
		}
		if (nrt + 1 < p) {
			e[nrt] = A[nrt][p - 1];
		}
		e[p - 1] = 0.0;

		// If required, generate U.

		if (wantu) {
			for (int j = nct; j < nu; j++) {
				for (int i = 0; i < this.m; i++) {
					this.U[i][j] = 0.0;
				}
				this.U[j][j] = 1.0;
			}
			for (int k = nct - 1; k >= 0; k--) {
				if (this.s[k] != 0.0) {
					for (int j = k + 1; j < nu; j++) {
						double t = 0;
						for (int i = k; i < this.m; i++) {
							t += this.U[i][k] * this.U[i][j];
						}
						t = -t / this.U[k][k];
						for (int i = k; i < this.m; i++) {
							this.U[i][j] += t * this.U[i][k];
						}
					}
					for (int i = k; i < this.m; i++) {
						this.U[i][k] = -this.U[i][k];
					}
					this.U[k][k] = 1.0 + this.U[k][k];
					for (int i = 0; i < k - 1; i++) {
						this.U[i][k] = 0.0;
					}
				} else {
					for (int i = 0; i < this.m; i++) {
						this.U[i][k] = 0.0;
					}
					this.U[k][k] = 1.0;
				}
			}
		}

		// If required, generate V.

		if (wantv) {
			for (int k = this.n - 1; k >= 0; k--) {
				if ((k < nrt) & (e[k] != 0.0)) {
					for (int j = k + 1; j < nu; j++) {
						double t = 0;
						for (int i = k + 1; i < this.n; i++) {
							t += this.V[i][k] * this.V[i][j];
						}
						t = -t / this.V[k + 1][k];
						for (int i = k + 1; i < this.n; i++) {
							this.V[i][j] += t * this.V[i][k];
						}
					}
				}
				for (int i = 0; i < this.n; i++) {
					this.V[i][k] = 0.0;
				}
				this.V[k][k] = 1.0;
			}
		}

		// Main iteration loop for the singular values.

		int pp = p - 1;
		int iter = 0;
		double eps = Math.pow(2.0, -52.0);
		double tiny = Math.pow(2.0, -966.0);
		while (p > 0) {
			int k, kase;

			// Here is where a test for too many iterations would go.

			// This section of the program inspects for
			// negligible elements in the s and e arrays. On
			// completion the variables kase and k are set as follows.

			// kase = 1 if s(p) and e[k-1] are negligible and k<p
			// kase = 2 if s(k) is negligible and k<p
			// kase = 3 if e[k-1] is negligible, k<p, and
			// s(k), ..., s(p) are not negligible (qr step).
			// kase = 4 if e(p-1) is negligible (convergence).

			for (k = p - 2; k >= -1; k--) {
				if (k == -1) {
					break;
				}
				if (Math.abs(e[k]) <= tiny + eps * (Math.abs(this.s[k]) + Math.abs(this.s[k + 1]))) {
					e[k] = 0.0;
					break;
				}
			}
			if (k == p - 2) {
				kase = 4;
			} else {
				int ks;
				for (ks = p - 1; ks >= k; ks--) {
					if (ks == k) {
						break;
					}
					double t = (ks != p ? Math.abs(e[ks]) : 0.) + (ks != k + 1 ? Math.abs(e[ks - 1]) : 0.);
					if (Math.abs(this.s[ks]) <= tiny + eps * t) {
						this.s[ks] = 0.0;
						break;
					}
				}
				if (ks == k) {
					kase = 3;
				} else if (ks == p - 1) {
					kase = 1;
				} else {
					kase = 2;
					k = ks;
				}
			}
			k++;

			// Perform the task indicated by kase.

			switch (kase) {

			// Deflate negligible s(p).

			case 1: {
				double f = e[p - 2];
				e[p - 2] = 0.0;
				for (int j = p - 2; j >= k; j--) {
					double t = EncogMath.hypot(this.s[j], f);
					double cs = this.s[j] / t;
					double sn = f / t;
					this.s[j] = t;
					if (j != k) {
						f = -sn * e[j - 1];
						e[j - 1] = cs * e[j - 1];
					}
					if (wantv) {
						for (int i = 0; i < this.n; i++) {
							t = cs * this.V[i][j] + sn * this.V[i][p - 1];
							this.V[i][p - 1] = -sn * this.V[i][j] + cs * this.V[i][p - 1];
							this.V[i][j] = t;
						}
					}
				}
			}
				break;

			// Split at negligible s(k).

			case 2: {
				double f = e[k - 1];
				e[k - 1] = 0.0;
				for (int j = k; j < p; j++) {
					double t = EncogMath.hypot(this.s[j], f);
					double cs = this.s[j] / t;
					double sn = f / t;
					this.s[j] = t;
					f = -sn * e[j];
					e[j] = cs * e[j];
					if (wantu) {
						for (int i = 0; i < this.m; i++) {
							t = cs * this.U[i][j] + sn * this.U[i][k - 1];
							this.U[i][k - 1] = -sn * this.U[i][j] + cs * this.U[i][k - 1];
							this.U[i][j] = t;
						}
					}
				}
			}
				break;

			// Perform one qr step.

			case 3: {

				// Calculate the shift.

				double scale = Math.max(Math.max(
						Math.max(Math.max(Math.abs(this.s[p - 1]), Math.abs(this.s[p - 2])), Math.abs(e[p - 2])),
						Math.abs(this.s[k])), Math.abs(e[k]));
				double sp = this.s[p - 1] / scale;
				double spm1 = this.s[p - 2] / scale;
				double epm1 = e[p - 2] / scale;
				double sk = this.s[k] / scale;
				double ek = e[k] / scale;
				double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
				double c = (sp * epm1) * (sp * epm1);
				double shift = 0.0;
				if ((b != 0.0) | (c != 0.0)) {
					shift = Math.sqrt(b * b + c);
					if (b < 0.0) {
						shift = -shift;
					}
					shift = c / (b + shift);
				}
				double f = (sk + sp) * (sk - sp) + shift;
				double g = sk * ek;

				// Chase zeros.

				for (int j = k; j < p - 1; j++) {
					double t = EncogMath.hypot(f, g);
					double cs = f / t;
					double sn = g / t;
					if (j != k) {
						e[j - 1] = t;
					}
					f = cs * this.s[j] + sn * e[j];
					e[j] = cs * e[j] - sn * this.s[j];
					g = sn * this.s[j + 1];
					this.s[j + 1] = cs * this.s[j + 1];
					if (wantv) {
						for (int i = 0; i < this.n; i++) {
							t = cs * this.V[i][j] + sn * this.V[i][j + 1];
							this.V[i][j + 1] = -sn * this.V[i][j] + cs * this.V[i][j + 1];
							this.V[i][j] = t;
						}
					}
					t = EncogMath.hypot(f, g);
					cs = f / t;
					sn = g / t;
					this.s[j] = t;
					f = cs * e[j] + sn * this.s[j + 1];
					this.s[j + 1] = -sn * e[j] + cs * this.s[j + 1];
					g = sn * e[j + 1];
					e[j + 1] = cs * e[j + 1];
					if (wantu && (j < this.m - 1)) {
						for (int i = 0; i < this.m; i++) {
							t = cs * this.U[i][j] + sn * this.U[i][j + 1];
							this.U[i][j + 1] = -sn * this.U[i][j] + cs * this.U[i][j + 1];
							this.U[i][j] = t;
						}
					}
				}
				e[p - 2] = f;
				iter = iter + 1;
			}
				break;

			// Convergence.

			case 4: {

				// Make the singular values positive.

				if (this.s[k] <= 0.0) {
					this.s[k] = (this.s[k] < 0.0 ? -this.s[k] : 0.0);
					if (wantv) {
						for (int i = 0; i <= pp; i++) {
							this.V[i][k] = -this.V[i][k];
						}
					}
				}

				// Order the singular values.

				while (k < pp) {
					if (this.s[k] >= this.s[k + 1]) {
						break;
					}
					double t = this.s[k];
					this.s[k] = this.s[k + 1];
					this.s[k + 1] = t;
					if (wantv && (k < this.n - 1)) {
						for (int i = 0; i < this.n; i++) {
							t = this.V[i][k + 1];
							this.V[i][k + 1] = this.V[i][k];
							this.V[i][k] = t;
						}
					}
					if (wantu && (k < this.m - 1)) {
						for (int i = 0; i < this.m; i++) {
							t = this.U[i][k + 1];
							this.U[i][k + 1] = this.U[i][k];
							this.U[i][k] = t;
						}
					}
					k++;
				}
				iter = 0;
				p--;
			}
				break;
			}
		}
	}

	/*
	 * ------------------------ Public Methods ------------------------
	 */

	/**
	 * Return the left singular vectors
	 *
	 * @return U
	 */

	public Matrix getU() {
		return new Matrix(this.U);
	}

	/**
	 * Return the right singular vectors
	 *
	 * @return V
	 */

	public Matrix getV() {
		return new Matrix(this.V);
	}

	/**
	 * Return the one-dimensional array of singular values
	 *
	 * @return diagonal of S.
	 */

	public double[] getSingularValues() {
		return this.s;
	}

	/**
	 * Return the diagonal matrix of singular values
	 *
	 * @return S
	 */

	public Matrix getS() {
		Matrix X = new Matrix(this.n, this.n);
		double[][] S = X.getData();
		for (int i = 0; i < this.n; i++) {
			for (int j = 0; j < this.n; j++) {
				S[i][j] = 0.0;
			}
			S[i][i] = this.s[i];
		}
		return X;
	}

	/**
	 * Two norm
	 *
	 * @return max(S)
	 */

	public double norm2() {
		return this.s[0];
	}

	/**
	 * Two norm condition number
	 *
	 * @return max(S)/min(S)
	 */

	public double cond() {
		return this.s[0] / this.s[Math.min(this.m, this.n) - 1];
	}

	/**
	 * Effective numerical matrix rank
	 *
	 * @return Number of nonnegligible singular values.
	 */

	public int rank() {
		double eps = Math.pow(2.0, -52.0);
		double tol = Math.max(this.m, this.n) * this.s[0] * eps;
		int r = 0;
		for (int i = 0; i < this.s.length; i++) {
			if (this.s[i] > tol) {
				r++;
			}
		}
		return r;
	}
}
