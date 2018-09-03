/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.opennars.lab.symv2.imageactorlayer;

import org.opennars.lab.common.math.DualNumber;
import org.opennars.lab.nn.FeedForwardNeuralNetwork;
import org.opennars.lab.nn.slim.Neuron;
import org.opennars.lab.nn.slim.SelfDelimitingNeuralNetwork;

import java.util.ArrayList;

/**
 * Actor which can wander around in the image
 */
public class ImageActor {
    public static class PositionAndOrientation {
        public float[] position;
        public float orientationInRadiants;
    }

    // is the search for the proto-edge terminated?
    public boolean searchTerminated = false;

    public PositionAndOrientation positionAndOrientation;

    public SelfDelimitingNeuralNetwork controlSlimRnn;
    public FeedForwardNeuralNetwork feedForwardNeuralNetwork;

    public int slimOutputNeuronIdx;

    public void setup() {
        populateFeedForwardNeuralNetwork();
        populateSlimRnn();
    }

    private void populateFeedForwardNeuralNetwork() {
        feedForwardNeuralNetwork = new FeedForwardNeuralNetwork();

        // setup hidden neurons

        feedForwardNeuralNetwork.hiddenNeurons = new DualNumber[2][];


        double[] realWeights = retWeightsForTurnLeftAction();
        feedForwardNeuralNetwork.hiddenNeurons[0] = new DualNumber[realWeights.length];
        for(int i=0;i<feedForwardNeuralNetwork.hiddenNeurons[0].length;i++) {
            feedForwardNeuralNetwork.hiddenNeurons[0][i] = new DualNumber(realWeights[i]);
        }

        realWeights = retWeightsForTurnRightAction();
        feedForwardNeuralNetwork.hiddenNeurons[1] = new DualNumber[realWeights.length];
        for(int i=0;i<feedForwardNeuralNetwork.hiddenNeurons[1].length;i++) {
            feedForwardNeuralNetwork.hiddenNeurons[1][i] = new DualNumber(realWeights[i]);
        }

        // setup output neurons

        // we just feed the hidden neurons to the output neurons

        feedForwardNeuralNetwork.outputNeurons = new DualNumber[2][];
        feedForwardNeuralNetwork.outputNeurons[0] = new DualNumber[2];
        feedForwardNeuralNetwork.outputNeurons[0][0] = new DualNumber(1.0);
        feedForwardNeuralNetwork.outputNeurons[0][1] = new DualNumber(0.0);

        feedForwardNeuralNetwork.outputNeurons[1] = new DualNumber[2];
        feedForwardNeuralNetwork.outputNeurons[1][0] = new DualNumber(0.0);
        feedForwardNeuralNetwork.outputNeurons[1][1] = new DualNumber(1.0);

        // we need to allocate the output activations
        feedForwardNeuralNetwork.outputActivations = new DualNumber[2];
        feedForwardNeuralNetwork.outputActivations[0] = new DualNumber(0.0);
        feedForwardNeuralNetwork.outputActivations[1] = new DualNumber(0.0);
    }



    private void populateSlimRnn() {
        controlSlimRnn = new SelfDelimitingNeuralNetwork();

        int numberOfNeurons = 0;
        numberOfNeurons += 2; // input neurons from FF-(R)NN
        numberOfNeurons += 1; // constant input neuron

        controlSlimRnn.haltNeuronIdx = numberOfNeurons;
        numberOfNeurons += 1; // termination neuron

        slimOutputNeuronIdx = numberOfNeurons;
        numberOfNeurons += 4; // output neurons, rotate left, rotate right, move, terminate

        // hidden neurons
        int terminationDelayNeurons = numberOfNeurons;
        numberOfNeurons += 1; // one neuron to delay the termination

        int neuronIdxForSubtractStimulus = numberOfNeurons;
        numberOfNeurons += 2; // neurons to subtract the two direction stimulus


        controlSlimRnn.neurons = new ArrayList<>();
        for(int c=0;c<numberOfNeurons;c++) {
            controlSlimRnn.neurons.add(new Neuron());
        }

        /////////////////
        // wire up neurons
        /////////////////

        controlSlimRnn.neurons.get(0).outputNeuronIndicesWithWeights.add(new Neuron.OutputNeuronIndexWithWeight(neuronIdxForSubtractStimulus, 1.0));
        controlSlimRnn.neurons.get(1).outputNeuronIndicesWithWeights.add(new Neuron.OutputNeuronIndexWithWeight(neuronIdxForSubtractStimulus, -1.0));

        controlSlimRnn.neurons.get(0).outputNeuronIndicesWithWeights.add(new Neuron.OutputNeuronIndexWithWeight(neuronIdxForSubtractStimulus+1, -1.0));
        controlSlimRnn.neurons.get(1).outputNeuronIndicesWithWeights.add(new Neuron.OutputNeuronIndexWithWeight(neuronIdxForSubtractStimulus+1, 1.0));

        controlSlimRnn.neurons.get(neuronIdxForSubtractStimulus).threshold = 0.1;
        controlSlimRnn.neurons.get(neuronIdxForSubtractStimulus).outputNeuronIndicesWithWeights.add(new Neuron.OutputNeuronIndexWithWeight(slimOutputNeuronIdx, 1.0));

        controlSlimRnn.neurons.get(neuronIdxForSubtractStimulus+1).threshold = 0.1;
        controlSlimRnn.neurons.get(neuronIdxForSubtractStimulus+1).outputNeuronIndicesWithWeights.add(new Neuron.OutputNeuronIndexWithWeight(slimOutputNeuronIdx +1, 1.0));
    }


    private static double[] retWeightsForTurnLeftAction() {
        return new double[]{0,0,1, 0,0,0, 0,0,0, 0,0,0,  0,  0, 0,0,0};
    }

    private static double[] retWeightsForTurnRightAction() {
        return new double[]{0,0,0, 0,0,0, 0,0,1, 0,0,0,  0,  0, 0,0,0};
    }
}
