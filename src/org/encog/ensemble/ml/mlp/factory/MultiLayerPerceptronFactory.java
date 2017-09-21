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
package org.encog.ensemble.ml.mlp.factory;

import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class MultiLayerPerceptronFactory implements EnsembleMLMethodFactory {

	List<Integer> layers;
	List<Double> dropoutRates;
	ActivationFunction activation;
	ActivationFunction lastLayerActivation;
	ActivationFunction firstLayerActivation;
	int sizeMultiplier = 1;

	public void setParameters(List<Integer> layers, ActivationFunction firstLayerActivation,
			ActivationFunction activation, ActivationFunction lastLayerActivation, List<Double> dropoutRates) {
		this.layers = layers;
		this.activation = activation;
		this.firstLayerActivation = firstLayerActivation;
		this.lastLayerActivation = lastLayerActivation;
		this.dropoutRates = dropoutRates;
	}

	public void setParameters(List<Integer> layers, ActivationFunction firstLayerActivation,
			ActivationFunction activation, ActivationFunction lastLayerActivation) {
		this.setParameters(layers, firstLayerActivation, activation, lastLayerActivation, null);
	}

	public void setParameters(List<Integer> layers, ActivationFunction firstLayerActivation,
			ActivationFunction activation) {
		this.setParameters(layers, firstLayerActivation, activation, activation, null);
	}

	public void setParameters(List<Integer> layers, ActivationFunction firstLayerActivation,
			ActivationFunction activation, List<Double> dropoutRates) {
		this.setParameters(layers, firstLayerActivation, activation, activation, dropoutRates);
	}

	public void setParameters(List<Integer> layers, ActivationFunction activation, List<Double> dropoutRates) {
		this.setParameters(layers, activation, activation, activation, dropoutRates);
	}

	public void setParameters(List<Integer> layers, ActivationFunction activation) {
		this.setParameters(layers, activation, activation, activation, null);
	}

	@Override
	public MLMethod createML(int inputs, int outputs) {
		BasicNetwork network = new BasicNetwork();
		if (this.dropoutRates != null) {
			network.addLayer(new BasicLayer(this.activation, false, inputs, this.dropoutRates.get(0))); // (inputs));
		} else {
			network.addLayer(new BasicLayer(this.activation, false, inputs)); // (inputs));
		}
		for (int i = 0; i < this.layers.size(); i++) {
			if (this.dropoutRates != null) {
				network.addLayer(new BasicLayer(this.activation, true, this.layers.get(i) * this.sizeMultiplier,
						this.dropoutRates.get(i + 1)));
			} else {
				network.addLayer(new BasicLayer(this.activation, true, this.layers.get(i) * this.sizeMultiplier));
			}
		}

		if (this.dropoutRates != null) {
			network.addLayer(new BasicLayer(this.lastLayerActivation, true, outputs,
					this.dropoutRates.get(this.dropoutRates.size() - 1)));
		} else {
			network.addLayer(new BasicLayer(this.lastLayerActivation, true, outputs));
		}
		network.getStructure().finalizeStructure(this.dropoutRates != null);
		network.reset();
		return network;
	}

	private String getLayerLabel(int i) {
		// dropoutRates contains the first and last layers as well
		if (this.dropoutRates != null && this.dropoutRates.size() > i + 2) {
			return this.layers.get(i).toString() + ":" + this.dropoutRates.get(i + 1).toString();
		} else {
			return this.layers.get(i).toString();
		}
	}

	@Override
	public String getLabel() {
		String ret = "mlp{";
		for (int i = 0; i < this.layers.size() - 1; i++) {
			ret = ret + this.getLayerLabel(i) + ",";
		}
		return ret + this.getLayerLabel(this.layers.size() - 1) + "}" + "-" + this.firstLayerActivation.getLabel() + ","
				+ this.activation.getLabel() + "," + this.lastLayerActivation.getLabel();
	}

	@Override
	public void reInit(MLMethod ml) {
		((BasicNetwork) ml).reset();
	}

	@Override
	public void setSizeMultiplier(int sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
	}

}