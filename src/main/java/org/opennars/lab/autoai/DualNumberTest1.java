package org.opennars.lab.autoai;

import org.opennars.lab.autoai.structure.Backpropagation;
import org.opennars.lab.autoai.structure.NetworkContext;
import org.opennars.lab.autoai.structure.NeuralNetworkLayer;
import org.opennars.lab.common.math.DualNumber;
import org.opennars.lab.common.math.DualNumberHelper;

public class DualNumberTest1 {
    public static void main2(String[] args) {
        DualNumber a = new DualNumber();

        a.real = 0.0;

        DualNumber b = new DualNumber();
        b.real = 1.0;
        b.diff = new double[]{0.0, 1.0};

        for(int iteration=0;iteration<50;iteration++) {
            a.diff = new double[]{1.0, 0.0};

            DualNumber diff = DualNumber.additiveRing(a, b, -1);

            double learnRate = 0.015;

            // adapt
            a.real = a.real - diff.real * a.diff[0] * learnRate;

            System.out.println(a.real);
        }

    }

    public static void main3(String[] args) {
        DualNumber a = new DualNumber();
        a.real = 0.0;

        DualNumber b = new DualNumber();
        b.real = 1.0;

        DualNumber t = new DualNumber();
        t.real = 0.6;
        t.diff = new double[]{0.0, 0.0};

        // learn to adapt a and b for fixed t for result

        for(int iteration=0;iteration<250;iteration++) {
            a.diff = new double[]{1.0, 0.0};
            b.diff = new double[]{0.0, 1.0};

            DualNumber expectedResult = new DualNumber(0.5);
            expectedResult.diff = new double[]{0.0, 0.0};

            DualNumber weight = DualNumberHelper.smoothStep(t);
            DualNumber result = new DualNumber(0.0);
            result.diff = new double[2];

            DualNumber _1 = new DualNumber(1.0);
            _1.diff = new double[2];

            // result = a * weight + b * (1.0 - weight)
            result = DualNumber.additiveRing(result, DualNumber.mul(a, weight), 1);
            result = DualNumber.additiveRing(result, DualNumber.mul(b, DualNumber.additiveRing(_1, weight, -1)), 1);

            DualNumber diff = DualNumber.additiveRing(result, expectedResult, -1);

            double learnRate = 0.03;

            // adapt
            a.real = a.real + diff.diff[0] * learnRate;
            b.real = b.real - diff.diff[1] * learnRate;

            System.out.println("diff=" + Double.toString(diff.real) + "   " + Double.toString(a.real) + " " + Double.toString(b.real));
        }

    }


    // basic backprop with one neuron which has 3 weights
    public static void main(String[] args) {
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
            }
            else {
                // calculate soft-max regression function

                // index of expected class
                int expectedClassification = 1;
                int numberOfClasses = 2;


                // see http://ufldl.stanford.edu/tutorial/supervised/SoftmaxRegression/
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
            }




            System.out.println("cost=" + Double.toString(cost.real));


            // adapt
            Backpropagation.backpropagate(cost, context);

            int debugMeHere = 5;
        }


        int here = 5;
    }

    public static class Neuron {
        public DualNumber[] weights;
        public DualNumber bias;

        public DualNumber computeActivation(final DualNumber[] inputFromPreviousLayer) {
            DualNumber result = new DualNumber(0.0);
            result.diff = new double[inputFromPreviousLayer[0].diff.length];

            for (int i=0;i<weights.length; i++) {
                DualNumber mul = DualNumber.mul(weights[i], inputFromPreviousLayer[i]);
                result = DualNumber.additiveRing(result, mul, 1);
            }

            result = DualNumber.additiveRing(result, bias, 1);

            return result;
        }
    }
}
