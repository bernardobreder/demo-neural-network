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
package com.heatonresearch.book.introneuralnet.ch3.hopfield;

import java.applet.Applet;
import java.awt.event.FocusEvent;

import com.heatonresearch.book.introneuralnet.neural.hopfield.HopfieldNetwork;

/**
 * Chapter 3: Using a Hopfield Neural Network
 *
 * HopfieldApplet: Applet that allows you to work with a Hopfield network.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class HopfieldApplet extends Applet implements java.awt.event.ActionListener, java.awt.event.FocusListener {

	private static final int CELL_WIDTH = 48;
	private static final int CELL_HEIGHT = 24;
	private static final int MAX = 5;
	/**
	 * Serial id for this class.
	 */
	HopfieldNetwork network = new HopfieldNetwork(MAX);

	java.awt.Label label1 = new java.awt.Label();
	java.awt.TextField matrix[][] = new java.awt.TextField[MAX][MAX];

	java.awt.Choice input[] = new java.awt.Choice[MAX];
	java.awt.Label label2 = new java.awt.Label();
	java.awt.TextField output[] = new java.awt.TextField[MAX];
	java.awt.Label label3 = new java.awt.Label();
	java.awt.Button go = new java.awt.Button();
	java.awt.Button train = new java.awt.Button();
	java.awt.Button clear = new java.awt.Button();

	/**
	 * Called when the user clicks one of the buttons.
	 *
	 * @param event
	 *            The event.
	 */
	@Override
	public void actionPerformed(final java.awt.event.ActionEvent event) {
		final Object object = event.getSource();
		if (object == this.go) {
			this.runNetwork();
		} else if (object == this.clear) {
			this.clear();
		} else if (object == this.train) {
			this.train();
		}
	}

	/**
	 * Clear the neural network.
	 */
	void clear() {
		this.network.getMatrix().clear();
		this.setMatrixValues();
	}

	/**
	 * Collect the matrix values from the applet and place inside the weight
	 * matrix for the neural network.
	 */
	private void collectMatrixValues() {
		for (int row = 0; row < MAX; row++) {
			for (int col = 0; col < MAX; col++) {
				final String str = this.matrix[row][col].getText();
				int value = 0;

				try {
					value = Integer.parseInt(str);
				} catch (final NumberFormatException e) {
					// let the value default to zero,
					// which it already is by this point.
				}

				// do not allow neurons to self-connect
				if (row == col) {
					this.network.getMatrix().set(row, col, 0);
				} else {
					this.network.getMatrix().set(row, col, value);
				}

			}
		}
	}

	@Override
	public void focusGained(final FocusEvent e) {
		// don't care

	}

	@Override
	public void focusLost(final FocusEvent e) {
		this.collectMatrixValues();
		this.setMatrixValues();
	}

	/**
	 * Setup the applet.
	 */
	@Override
	public void init() {
		this.setLayout(null);
		this.label1.setText("Enter the activation weight matrix:");
		this.add(this.label1);
		int textHeight = 12;
		this.label1.setBounds(24, textHeight, 192, textHeight);
		this.label2.setText("Input pattern to run or train:");
		this.add(this.label2);
		this.label2.setBounds(24, pointYMatrixCell(MAX), 192, textHeight);

		for (int row = 0; row < MAX; row++) {
			for (int col = 0; col < MAX; col++) {
				this.matrix[row][col] = new java.awt.TextField();
				this.add(this.matrix[row][col]);
				this.matrix[row][col].setBounds(pointXMatrixCell(col), pointYMatrixCell(row), CELL_WIDTH, CELL_HEIGHT);
				this.matrix[row][col].setText("0");
				this.matrix[row][col].addFocusListener(this);

				if (row == col) {
					this.matrix[row][col].setEnabled(false);
				}

			}
		}

		for (int i = 0; i < MAX; i++) {
			this.output[i] = new java.awt.TextField();
			this.output[i].setEditable(false);
			this.output[i].setText("0");
			this.output[i].setEnabled(true);
			this.add(this.output[i]);
			this.output[i].setBounds(pointXMatrixCell(i), pointYMatrixCell(MAX) + 6 * textHeight + CELL_HEIGHT,
					CELL_WIDTH, CELL_HEIGHT);

			this.input[i] = new java.awt.Choice();
			this.input[i].add("0");
			this.input[i].add("1");
			this.input[i].select(0);
			this.add(this.input[i]);
			this.input[i].setBounds(pointXMatrixCell(i), pointYMatrixCell(MAX) + 2 * textHeight, 60, CELL_HEIGHT);
		}

		this.label3.setText("The output is:");
		this.add(this.label3);
		this.label3.setBounds(24, 276, pointYMatrixCell(MAX) + 4 * textHeight + CELL_HEIGHT, textHeight);

		int buttonY = pointYMatrixCell(MAX) + 2 * textHeight + 2 * CELL_HEIGHT;
		this.go.setLabel("Run");
		this.add(this.go);
		this.go.setBackground(java.awt.Color.lightGray);
		this.go.setBounds(10, 240, 80, 24);

		this.train.setLabel("Train");
		this.add(this.train);
		this.train.setBackground(java.awt.Color.lightGray);
		this.train.setBounds(110, 240, 80, 24);

		this.clear.setLabel("Clear");
		this.add(this.clear);
		this.clear.setBackground(java.awt.Color.lightGray);
		this.clear.setBounds(210, 240, 80, 24);

		this.go.addActionListener(this);
		this.clear.addActionListener(this);
		this.train.addActionListener(this);

		setSize(getPreferredSize());
	}

	public int pointXMatrixCell(int col) {
		return 24 + (col * 60);
	}

	public int pointYMatrixCell(int row) {
		return 36 + (row * 38);
	}

	/**
	 * Collect the input, present it to the neural network, then display the
	 * results.
	 */
	void runNetwork() {

		final boolean pattern[] = new boolean[MAX];

		// Read the input into a boolean array.
		for (int row = 0; row < MAX; row++) {
			final int i = this.input[row].getSelectedIndex();
			if (i == 0) {
				pattern[row] = false;
			} else {
				pattern[row] = true;
			}
		}

		// Present the input to the neural network.
		final boolean result[] = this.network.present(pattern);

		// Display the result.
		for (int row = 0; row < MAX; row++) {
			if (result[row]) {
				this.output[row].setText("1");
			} else {
				this.output[row].setText("0");
			}

			// If the result is different than the input, show in yellow.
			if (result[row] == pattern[row]) {
				this.output[row].setBackground(java.awt.Color.white);
			} else {
				this.output[row].setBackground(java.awt.Color.yellow);
			}
		}

	}

	/**
	 * Set the matrix values on the applet from the matrix values stored in the
	 * neural network.
	 */
	private void setMatrixValues() {

		for (int row = 0; row < MAX; row++) {
			for (int col = 0; col < MAX; col++) {
				this.matrix[row][col].setText("" + (int) this.network.getMatrix().get(row, col));
			}
		}
	}

	/**
	 * Called when the train button is clicked. Train for the current pattern.
	 */
	void train() {
		final boolean[] booleanInput = new boolean[MAX];

		// Collect the input pattern.
		for (int x = 0; x < MAX; x++) {
			booleanInput[x] = (this.input[x].getSelectedIndex() != 0);
		}

		// Train the input pattern.
		this.network.train(booleanInput);
		this.setMatrixValues();

	}

}
