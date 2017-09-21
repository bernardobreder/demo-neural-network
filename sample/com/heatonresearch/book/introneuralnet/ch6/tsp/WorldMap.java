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
package com.heatonresearch.book.introneuralnet.ch6.tsp;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Chapter 6: Training using a Genetic Algorithm
 *
 * WorldMap: Holds the map for the traveling salesman problem.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class WorldMap extends JPanel {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 6412464565412351889L;
	/**
	 * The TravelingSalesman object that owns this object.
	 */
	protected GeneticTravelingSalesmanFrame owner;

	/**
	 * Constructor.
	 *
	 * @param owner
	 *            The TravelingSalesman object that owns this object.
	 */
	WorldMap(final GeneticTravelingSalesmanFrame owner) {
		this.owner = owner;
	}

	/**
	 * Update the graphical display of the map.
	 *
	 * @param g
	 *            The graphics object to use.
	 */
	@Override
	public void paint(final Graphics g) {
		this.update(g);
	}

	/**
	 * Update the graphical display of the map.
	 *
	 * @param g
	 *            The graphics object to use.
	 */
	@Override
	public void update(final Graphics g) {
		final int width = this.getBounds().width;
		final int height = this.getBounds().height;

		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);

		if (!this.owner.started) {
			return;
		}

		g.setColor(Color.green);
		for (int i = 0; i < GeneticTravelingSalesmanFrame.CITY_COUNT; i++) {
			final int xpos = this.owner.cities[i].getx();
			final int ypos = this.owner.cities[i].gety();
			g.fillOval(xpos - 5, ypos - 5, 10, 10);
		}

		final TSPChromosome top = this.owner.getTopChromosome();

		g.setColor(Color.white);
		for (int i = 0; i < GeneticTravelingSalesmanFrame.CITY_COUNT - 1; i++) {
			final int icity = top.getGene(i);
			final int icity2 = top.getGene(i + 1);

			g.drawLine(this.owner.cities[icity].getx(), this.owner.cities[icity].gety(),
					this.owner.cities[icity2].getx(), this.owner.cities[icity2].gety());

		}
	}

}
