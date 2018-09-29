package org.opennars.lab.autoai.hyperparameteroptimization;

import org.opennars.lab.autoai.structure.Backpropagation;
import org.opennars.lab.autoai.structure.NetworkContext;
import org.opennars.lab.autoai.structure.NeuralNetworkLayer;
import org.opennars.lab.autoai.structure.Neuron;
import org.opennars.lab.common.math.DualNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to evaluate the performance (loss for neural-networks) of a artifact under test, such as neural-networks
 */
// TODO< make this a interface and make classes for it >
public class ArtifactEvaluator {
    /** configurations of each layer */
    // ASSUMPTION< "linear" feed forward network >
    // TODO< refactor into graph representation >
    public LayerConfiguration[] layerConfigurations;

    /** a (deep) neural network can have multiple outputs */
    public LayerConfiguration[] outputsConfigurations;

    public double learnRate = 0.002;

    public int numberOfTrainingSteps = 500;

    public List<TrainingSample> trainingSamples = new ArrayList<>();

    public int numberOfInputs = 0;


    // learned weights
    private NeuralNetworkLayer[] layers;
    private NeuralNetworkLayer[] outputLayer;

    private NetworkContext context;

    /**
     *
     * @return metric of performance of artifact in question - lower is better
     */
    public double evaluate() {
        context = new NetworkContext();
        context.iDiffCounter = 0;
        context.sizeOfDiff = 0;

        context.learnRate = learnRate;


        layers = new NeuralNetworkLayer[layerConfigurations.length];
        outputLayer = new NeuralNetworkLayer[outputsConfigurations.length];

        int inputWidthOfPreviousLayer = numberOfInputs;

        for (int layerIdx=0; layerIdx<layerConfigurations.length; layerIdx++) {
            final int widthOfThisLayer = layerConfigurations[layerIdx].numberOfNeurons;

            layers[layerIdx] = new NeuralNetworkLayer(inputWidthOfPreviousLayer, layerConfigurations[layerIdx].activationFunction);
            layers[layerIdx].neurons = new Neuron[widthOfThisLayer];

            inputWidthOfPreviousLayer = widthOfThisLayer;
        }

        for (int layerIdx=0;layerIdx<outputsConfigurations.length; layerIdx++) {
            outputLayer[layerIdx] = new NeuralNetworkLayer(inputWidthOfPreviousLayer, outputsConfigurations[layerIdx].activationFunction);
            outputLayer[layerIdx].neurons = new Neuron[outputsConfigurations[layerIdx].numberOfNeurons];
        }


        {
            // count all differentials
            for(NeuralNetworkLayer iLayer:layers) {
                iLayer.build(context, true);
            }

            for(NeuralNetworkLayer iLayer:outputLayer) {
                iLayer.build(context, true);
            }

            context.sizeOfDiff = context.iDiffCounter;
            context.iDiffCounter = 0;

            // "real" building
            for(NeuralNetworkLayer iLayer:layers) {
                iLayer.build(context, false);
            }

            for(NeuralNetworkLayer iLayer:outputLayer) {
                iLayer.build(context, false);
            }
        }

        // minimal cost over multiple runs
        double minimalCost = Double.POSITIVE_INFINITY;

        int numberOfTries = 5;

        // we run multiple times and search the run with the lowest
        for (int iTry=0;iTry<numberOfTries;iTry++) {
            for(int iteration=0;iteration<numberOfTrainingSteps;iteration++) {

                /// cost as score - lower is better - is always positive
                double costAsScore = 0;

                DualNumber cost = new DualNumber(0.0);
                cost.diff = new double[context.sizeOfDiff];

                for(final TrainingSample iTrainingSample : trainingSamples) {
                    final DualNumber[][] activationsOfOutputLayers = forwardPropagate(iTrainingSample.input);


                    // iterate over all output layers
                    for (int iOutputLayer = 0; iOutputLayer < outputsConfigurations.length; iOutputLayer++) {

                        DualNumber[] outputActivations = activationsOfOutputLayers[iOutputLayer];


                        if (outputsConfigurations[iOutputLayer].activationFunction != NeuralNetworkLayer.EnumActivationFunction.SOFTMAX) {
                            TrainingSample.ExpectedResult expectedResultOfLayer = (TrainingSample.ExpectedResult)iTrainingSample.output[iOutputLayer];

                            DualNumber[] differences = new DualNumber[expectedResultOfLayer.values.length];

                            /// compare to expected output
                            for (int outputNeuronIdx=0; outputNeuronIdx<differences.length; outputNeuronIdx++) {
                                DualNumber expectedResult = new DualNumber(expectedResultOfLayer.values[outputNeuronIdx]);
                                expectedResult.diff = new double[context.sizeOfDiff];
                                differences[outputNeuronIdx] = DualNumber.additiveRing(outputActivations[outputNeuronIdx], expectedResult, -1);
                            }


                            DualNumber costAdditive = new DualNumber(0.0);
                            costAdditive.diff = new double[context.sizeOfDiff];

                            // calculate linear regression cost function
                            for (final DualNumber iDiff : differences) {
                                DualNumber _0p5 = new DualNumber(0.5);
                                _0p5.diff = new double[context.sizeOfDiff];

                                final DualNumber squaredError = DualNumber.mul(iDiff, iDiff);
                                final DualNumber halfSquaredError = DualNumber.mul(squaredError, _0p5);

                                costAdditive = DualNumber.additiveRing(costAdditive, halfSquaredError, 1);
                            }

                            cost = DualNumber.additiveRing(cost, costAdditive, 1);

                            costAsScore += costAdditive.real;
                        } else {
                            // calculate soft-max regression function

                            /// index of expected class
                            /// TODO< fetch expected class and number of classes from training sample >
                            int expectedClassification = ((TrainingSample.ExpectedSoftmaxOutput)iTrainingSample.output[iOutputLayer]).class_;
                            int numberOfClasses = outputsConfigurations[iOutputLayer].numberOfNeurons;

                            DualNumber costAdditive = new DualNumber(0.0);
                            costAdditive.diff = new double[context.sizeOfDiff];


                            /// see http://ufldl.stanford.edu/tutorial/supervised/SoftmaxRegression/
                            for (int i = 0; i < outputActivations.length; i++) {
                                for (int k = 0; k < numberOfClasses; k++) {
                                    final boolean isCorrectClassification = i == k && k == expectedClassification;
                                    if (!isCorrectClassification) {
                                        continue;
                                    }

                                    DualNumber log = DualNumber.log(outputActivations[i]);
                                    costAdditive = DualNumber.additiveRing(costAdditive, log, 1);
                                }
                            }

                            DualNumber _null = new DualNumber(0);
                            _null.diff = new double[context.sizeOfDiff];
                            costAdditive = DualNumber.additiveRing(_null, costAdditive, -1);

                            cost = DualNumber.additiveRing(cost, costAdditive, 1);

                            /// negative because logarithm is negative
                            costAsScore += -costAdditive.real;
                        }
                    }
                }


                System.out.println("iteration=" + Integer.toString(iteration)  + " cost=" + Double.toString(cost.real));



                // adapt
                Backpropagation.backpropagate(cost, context);

                // update minimal cost
                minimalCost = Math.min(minimalCost, costAsScore);

                if (Math.abs(cost.real) < 1.0e-8) {
                    break;
                }

                int debugMeHere = 5;
            }
        }



        return minimalCost;
    }

    public DualNumber[][] forwardPropagate(final double[] inputActivation) {
        DualNumber[] inputActivationAsDualNumber = new DualNumber[inputActivation.length];
        for (int i = 0; i < inputActivation.length; i++) {
            inputActivationAsDualNumber[i] = new DualNumber(inputActivation[i]);
            inputActivationAsDualNumber[i].diff = new double[context.sizeOfDiff];
        }


        // forward propagate
        DualNumber[] activationsOfPreviousLayer = inputActivationAsDualNumber;

        int layerIdx = 0;
        for (NeuralNetworkLayer iLayer : layers) {
            activationsOfPreviousLayer = iLayer.activate(context, activationsOfPreviousLayer);

            if (false) {

                System.out.println("activation of layer[" + Integer.toString(layerIdx) + "]:");

                for (int i = 0; i < activationsOfPreviousLayer.length; i++) {
                    System.out.print(Double.toString(activationsOfPreviousLayer[i].real) + " ");
                }

                System.out.println();
            }

            layerIdx++;
        }

        DualNumber[][] activationsOfOutputLayers = new DualNumber[outputsConfigurations.length][];

        // activate output layers
        for (int iOutputLayer = 0; iOutputLayer < outputsConfigurations.length; iOutputLayer++) {
            activationsOfOutputLayers[iOutputLayer] = outputLayer[iOutputLayer].activate(context, activationsOfPreviousLayer);

            if (false) {
                System.out.println("activation of outputlayer[" + Integer.toString(iOutputLayer) + "]:");

                for (int i = 0; i < activationsOfOutputLayers[iOutputLayer].length; i++) {
                    System.out.print(Double.toString(activationsOfOutputLayers[iOutputLayer][i].real) + " ");
                }

                System.out.println();
            }
        }
        return activationsOfOutputLayers;
    }

    public static class LayerConfiguration {
        public final NeuralNetworkLayer.EnumActivationFunction activationFunction;
        public final int numberOfNeurons;

        public LayerConfiguration(final NeuralNetworkLayer.EnumActivationFunction activationFunction, final int numberOfNeurons) {
            this.activationFunction = activationFunction;
            this.numberOfNeurons = numberOfNeurons;
        }
    }

    public static class TrainingSample {
        public double[] input;

        public ExpectedOutput[] output;

        public static abstract class ExpectedOutput {}

        public static class ExpectedSoftmaxOutput extends ExpectedOutput {
            public final int class_;

            public ExpectedSoftmaxOutput(final int class_) {
                this.class_ = class_;
            }
        }

        public static class ExpectedResult extends ExpectedOutput {
            public final double[] values;

            public ExpectedResult(final double[] values) {
                this.values = values;
            }
        }
    }
}
