package org.opennars.lab.autoai;

import org.opennars.lab.autoai.structure.Backpropagation;
import org.opennars.lab.autoai.structure.NetworkContext;
import org.opennars.lab.autoai.structure.NeuralNetworkLayer;
import org.opennars.lab.autoai.structure.Neuron;
import org.opennars.lab.common.math.DualNumber;

/**
 * Used to evaluate the performance (loss for neural-networks) of a artifact under test, such as neural-networks
 */
// TODO< make this a interface and make classes for it >
public class ArtifactEvaluator {
    /**
     *
     * @return metric of performance of artifact in question - lower is better
     */
    public double evaluate() {
        NetworkContext context = new NetworkContext();
        context.iDiffCounter = 0;
        context.sizeOfDiff = 0;

        context.learnRate = 0.003;


        NeuralNetworkLayer[] layers = new NeuralNetworkLayer[3];


        layers[0] = new NeuralNetworkLayer(3, NeuralNetworkLayer.EnumActivationFunction.RELU);
        layers[0].neurons = new Neuron[15];

        layers[1] = new NeuralNetworkLayer(15, NeuralNetworkLayer.EnumActivationFunction.RELU);
        layers[1].neurons = new Neuron[10];

        layers[2] = new NeuralNetworkLayer(10, NeuralNetworkLayer.EnumActivationFunction.RELU);
        layers[2].neurons = new Neuron[2];



        {
            // count all differentials
            for(NeuralNetworkLayer iLayer:layers) {
                iLayer.build(context, true);
            }

            context.sizeOfDiff = context.iDiffCounter;
            context.iDiffCounter = 0;

            // "real" building
            for(NeuralNetworkLayer iLayer:layers) {
                iLayer.build(context, false);
            }
        }

        // minimal cost over multiple runs
        double minimalCost = Double.POSITIVE_INFINITY;

        int numberOfTries = 5;

        // we run multiple times and search the run with the lowest
        for (int iTry=0;iTry<numberOfTries;iTry++) {
            for(int iteration=0;iteration<500;iteration++) {
                double[] inputActivation = new double[]{1.0, 0.5, 0.4};

                DualNumber[] inputActivationAsDualNumber = new DualNumber[inputActivation.length];
                for(int i=0;i<inputActivation.length;i++) {
                    inputActivationAsDualNumber[i] = new DualNumber(inputActivation[i]);
                    inputActivationAsDualNumber[i].diff = new double[context.sizeOfDiff];
                }


                // forward propagate
                DualNumber[] activationsOfPreviousLayer = inputActivationAsDualNumber;

                int layerIdx=0;
                for(NeuralNetworkLayer iLayer:layers) {
                    activationsOfPreviousLayer = iLayer.activate(context, activationsOfPreviousLayer);

                    System.out.println("activation of layer[" + Integer.toString(layerIdx) + "]:");

                    for(int i=0;i<activationsOfPreviousLayer.length;i++) {
                        System.out.print(Double.toString(activationsOfPreviousLayer[i].real) + " ");
                    }

                    System.out.println();

                    layerIdx++;
                }

                DualNumber[] outputActivations = activationsOfPreviousLayer;


                /// cost as score - lower is better - is always positive
                double costAsScore = 0;

                DualNumber cost = new DualNumber(0.0);
                cost.diff = new double[context.sizeOfDiff];

                if(false) {
                    DualNumber[] differences = new DualNumber[2];

                    DualNumber expectedResult = new DualNumber(0.7);
                    expectedResult.diff = new double[context.sizeOfDiff];
                    differences[0] = DualNumber.additiveRing(outputActivations[0], expectedResult, -1);

                    expectedResult = new DualNumber(0.1);
                    expectedResult.diff = new double[context.sizeOfDiff];
                    differences[1] = DualNumber.additiveRing(outputActivations[1], expectedResult, -1);



                    // calculate linear regression cost function
                    for(final DualNumber iDiff : differences) {
                        DualNumber _0p5 = new DualNumber(0.5);
                        _0p5.diff = new double[context.sizeOfDiff];

                        final DualNumber squaredError = DualNumber.mul(iDiff, iDiff);
                        final DualNumber halfSquaredError = DualNumber.mul(squaredError, _0p5);

                        cost = DualNumber.additiveRing(cost, halfSquaredError, 1);
                    }

                    costAsScore = cost.real;
                }
                else {
                    // calculate soft-max regression function

                    /// index of expected class
                    int expectedClassification = 1;
                    int numberOfClasses = 2;


                    /// see http://ufldl.stanford.edu/tutorial/supervised/SoftmaxRegression/
                    for (int i=0; i<outputActivations.length; i++) {
                        for (int k=0; k<numberOfClasses; k++) {
                            final boolean isCorrectClassification = i == k && k == expectedClassification;
                            if (!isCorrectClassification) {
                                continue;
                            }

                            DualNumber log = DualNumber.log(outputActivations[i]);
                            cost = DualNumber.additiveRing(cost, log, 1);
                        }
                    }

                    DualNumber _null = new DualNumber(0);
                    _null.diff = new double[context.sizeOfDiff];
                    cost = DualNumber.additiveRing(_null, cost, -1);

                    /// negative because logarithm is negative
                    costAsScore = -cost.real;
                }




                System.out.println("cost=" + Double.toString(cost.real));


                // adapt
                Backpropagation.backpropagate(cost, context);

                // update minimal cost
                minimalCost = Math.min(minimalCost, costAsScore);


                int debugMeHere = 5;
            }
        }



        return minimalCost;
    }
}
