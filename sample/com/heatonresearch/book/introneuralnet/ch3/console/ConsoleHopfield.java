/**
 * Introduction to Neural Networks with Java, 2nd Edition Copyright 2008 by
 * Heaton Research, Inc. http://www.heatonresearch.com/books/java-neural-2/
 *
 * ISBN13: 978-1-60439-008-7 ISBN: 1-60439-008-5
 *
 * This class is released under the: GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 */
package com.heatonresearch.book.introneuralnet.ch3.console;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.heatonresearch.book.introneuralnet.neural.hopfield.HopfieldNetwork;
import com.heatonresearch.book.introneuralnet.neural.som.SelfOrganizingMap;

/**
 * Chapter 3: Using a Hopfield Neural Network
 *
 * ConsoleHopfield: Simple console application that shows how to use a Hopfield
 * Neural Network.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class ConsoleHopfield {

	private static final boolean[] BOOLEANS = new boolean[] { true, false };

	/**
	 * Convert a boolean array to the form [T,T,F,F]
	 *
	 * @param b
	 *            A boolen array.
	 * @return The boolen array in string form.
	 */
	public static String formatBoolean(final boolean b[]) {
		final StringBuilder result = new StringBuilder();
		result.append('[');
		for (int i = 0; i < b.length; i++) {
			if (b[i]) {
				result.append("T");
			} else {
				result.append("F");
			}
			if (i != b.length - 1) {
				result.append(",");
			}
		}
		result.append(']');
		return (result.toString());
	}

	/**
	 * A simple main method to test the Hopfield neural network.
	 *
	 * @param args
	 *            Not used.
	 */
	public static void main(final String args[]) {
		int size = 5;
		final HopfieldNetwork network = new HopfieldNetwork(size);
		int max = 16;
		for (int i = 0; i < max; i++) {
			train(network, new boolean[] { true, false, false, false, true });
		}
		train(network, new boolean[] { true, false, false, true, true });
		train(network, new boolean[] { true, false, false, true, true });
		train(network, new boolean[] { true, false, false, true, true });
		Stream.of(create(size))
				.collect(Collectors.toMap(e -> e, e -> SelfOrganizingMap.outputNormalize(network.weight(e)))).entrySet()
				.stream().sorted((a, b) -> a.getValue().intValue() - b.getValue().intValue())
				.collect(Collectors.toList()).forEach(e -> {
					System.out.println(
							String.format("Pattern: %s with value: %f", formatBoolean(e.getKey()), e.getValue()));
				});
	}

	public static boolean[][] create(int size) {
		if (size == 1) {
			return new boolean[][] { new boolean[] { true }, new boolean[] { false } };
		}
		boolean[][] flat = create(size - 1);
		boolean[][] array = new boolean[2 * flat.length][];
		for (int i = 0; i < flat.length; i++) {
			boolean[] item = new boolean[size];
			item[0] = true;
			for (int j = 0; j < size - 1; j++) {
				item[j + 1] = flat[i][j];
			}
			array[i] = item;
		}
		for (int i = 0; i < flat.length; i++) {
			boolean[] item = new boolean[size];
			item[0] = false;
			for (int j = 0; j < size - 1; j++) {
				item[j + 1] = flat[i][j];
			}
			array[i + flat.length] = item;
		}
		return array;
	}

	public static boolean[] create(boolean[][] flat, boolean flag) {
		boolean[] item = new boolean[flat.length + 1];
		for (int i = 0; i < flat.length; i++) {
			item[0] = flag;
			for (int j = 0; j < flat.length; j++) {
				item[j + 1] = flat[i][j];
			}
		}
		return item;
	}

	public static void train(final HopfieldNetwork network, final boolean[] pattern) {
		System.out.println("Training Hopfield network with: " + formatBoolean(pattern));
		network.train(pattern);
	}

	public static void present(final HopfieldNetwork network, final boolean[] pattern) {
		System.out.println("Presenting pattern:" + formatBoolean(pattern) + ", and got "
				+ formatBoolean(network.present(pattern)) + " weight: " + network.weight(pattern));
	}

}
