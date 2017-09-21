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
package com.heatonresearch.book.introneuralnet.ch3.console;

import com.heatonresearch.book.introneuralnet.neural.hopfield.HopfieldNetwork;

/**
 * Chapter 3: Using a Hopfield Neural Network
 *
 * ConsoleHopfield: Simple console application that shows how to use a Hopfield
 * Neural Network.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class ConsoleHopfield2 extends ConsoleHopfield {

	/**
	 * A simple main method to test the Hopfield neural network.
	 *
	 * @param args
	 *            Not used.
	 */
	public static void main(final String args[]) {

		HopfieldNetwork network = new HopfieldNetwork(4);

		network.train(new boolean[] { false, false, true, true });
		// network.train(new boolean[] { true, true, false, false });
		// network.train(new boolean[] { false, true, true, false });
		network.train(new boolean[] { false, false, true, true });
		// network.train(new boolean[] { true, true, false, false });
		// network.train(new boolean[] { false, true, true, false });
		network.train(new boolean[] { false, false, true, true });
		// network.train(new boolean[] { true, true, false, false });
		// network.train(new boolean[] { false, true, true, false });
		network.train(new boolean[] { false, false, true, true });
		// network.train(new boolean[] { true, true, false, false });
		// network.train(new boolean[] { false, true, true, false });
		network.train(new boolean[] { false, false, true, true });
		// network.train(new boolean[] { true, true, false, false });
		// network.train(new boolean[] { false, true, true, false });

		System.out.println(formatBoolean(network.present(new boolean[] { false, false, true, false })));

	}

}
