package org.opennars.lab.autoai.structure;

import org.opennars.lab.autoai.DualNumberTest1;
import org.opennars.lab.autoai.NetworkContext;
import org.opennars.lab.autoai.Utilities;
import org.opennars.lab.common.math.DualNumber;

/**
 * A re-routeable layer of a neural network
 *
 * @author Robert Wünsche
 */
public class NeuralNetworkLayer {
    /**  */
    public DualNumberTest1.Neuron[] neurons;

    /**
     * @param context
     * @param countDifferentials
     */
    // called at setup time
    public void build(NetworkContext context, final boolean countDifferentials) {
        for (int iNeuron = 0; iNeuron < neurons.length; iNeuron++) {
            int numberOfWeightsOfThisNeuron = 3;

            if(countDifferentials) {
                context.iDiffCounter+=numberOfWeightsOfThisNeuron;
                context.iDiffCounter++;
            }
            else {
                neurons[iNeuron] = new DualNumberTest1.Neuron();
                neurons[iNeuron].weights = new DualNumber[numberOfWeightsOfThisNeuron];

                for (int iWeightIdx=0;iWeightIdx<numberOfWeightsOfThisNeuron;iWeightIdx++) {
                    DualNumber weight = new DualNumber(context.rng.nextDouble() * 2 - 1);
                    weight.diff = Utilities.makeArrWithOnehot(context.sizeOfDiff, context.iDiffCounter);

                    // keep track of mapping of differential to actual weight/value
                    context.mapDiffToDualNumber.add(weight);

                    neurons[iNeuron].weights[iWeightIdx] = weight;

                    context.iDiffCounter++;
                }

                // bias
                {
                    DualNumber bias = new DualNumber(context.rng.nextDouble() * 2 - 1);
                    bias.diff = Utilities.makeArrWithOnehot(context.sizeOfDiff, context.iDiffCounter);

                    // keep track of mapping of differential to actual weight/value
                    context.mapDiffToDualNumber.add(bias);

                    neurons[iNeuron].bias = bias;
                    context.iDiffCounter++;
                }
            }
        }
    }

    public DualNumber[] activate(final NetworkContext context, final DualNumber[] input) {
        DualNumber[] activations = new DualNumber[neurons.length];

        for (int iNeuronIdx=0;iNeuronIdx<neurons.length;iNeuronIdx++) {
            DualNumber x = neurons[iNeuronIdx].computeActivation(input);
            activations[iNeuronIdx] = activationFunction(context, x);
        }

        return activations;
    }

    public DualNumber activationFunction(final NetworkContext context, final DualNumber x) {
        DualNumber zero = new DualNumber(0);
        zero.diff = new double[context.sizeOfDiff];
        return DualNumber.max(x, zero);
    }
}
