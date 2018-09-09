package org.opennars.lab.autoai.structure;

import org.opennars.lab.autoai.DualNumberTest1;
import org.opennars.lab.autoai.Utilities;
import org.opennars.lab.common.math.DualNumber;

/**
 * A re-routeable layer of a neural network
 *
 * @author Robert Wünsche
 */
public class NeuralNetworkLayer {
    /** all neuron of this layer (element) */
    public DualNumberTest1.Neuron[] neurons;

    public final int inputWidth;

    public final EnumActivationFunction activationFunction;

    public NeuralNetworkLayer(final int inputWidth, EnumActivationFunction activationFunction) {
        this.inputWidth = inputWidth;
        this.activationFunction = activationFunction;
    }

    /**
     * @param context
     * @param countDifferentials
     */
    // called at setup time
    public void build(NetworkContext context, final boolean countDifferentials) {
        for (int iNeuron = 0; iNeuron < neurons.length; iNeuron++) {
            int numberOfWeightsOfThisNeuron = inputWidth;

            if(countDifferentials) {
                context.iDiffCounter+=numberOfWeightsOfThisNeuron;
                context.iDiffCounter++;
            }
            else {
                neurons[iNeuron] = new DualNumberTest1.Neuron();
                neurons[iNeuron].weights = new DualNumber[numberOfWeightsOfThisNeuron];

                for (int iWeightIdx=0;iWeightIdx<numberOfWeightsOfThisNeuron;iWeightIdx++) {
                    DualNumber weight = new DualNumber(context.centralDistribution.sample());
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
            activations[iNeuronIdx] = activationFunction(context, input, x);
        }

        return activations;
    }

    /**
     * compute the activation function
     *
     * @param context neural network context
     * @param activation input into all neurons
     * @param x the input of this neuron
     * @return activation function value
     */
    protected DualNumber activationFunction(final NetworkContext context, final DualNumber[] activation, final DualNumber x) {
        switch (activationFunction) {
            case RELU:
            return activationFunctionReLu(context, x);
        }

        assert activationFunction == EnumActivationFunction.SOFTMAX;
        return activationFunctionSoftmax(context, activation, x);
    }

    /**
     * compute the ReLu activation function
     *
     * @param context neural network context
     * @param x the input of this neuron
     * @return activation function value
     */
    private static DualNumber activationFunctionReLu(final NetworkContext context, final DualNumber x) {
        DualNumber zero = new DualNumber(0);
        zero.diff = new double[context.sizeOfDiff];
        return DualNumber.max(x, zero);
    }

    /**
     * compute the SoftMax activation function
     *
     * @param context neural network context
     * @param activation input into all neurons
     * @param x the input of this neuron
     * @return activation function value
     */
    // https://youtu.be/-7scQpJT7uo?t=7m25s
    private static DualNumber activationFunctionSoftmax(final NetworkContext context, final DualNumber[] activation, final DualNumber x) {
        DualNumber sumOfAllOther = new DualNumber(0);
        sumOfAllOther.diff = new double[context.sizeOfDiff];

        for (DualNumber iOther : activation) {
            final DualNumber thisExp = DualNumber.exp(iOther);
            sumOfAllOther = DualNumber.additiveRing(sumOfAllOther, thisExp, 1);
        }


        DualNumber thisExp = DualNumber.exp(x);

        return DualNumber.div(thisExp, sumOfAllOther);
    }

    public enum EnumActivationFunction {
        RELU,
        SOFTMAX,
    }
}
