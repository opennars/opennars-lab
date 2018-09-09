package org.opennars.lab.autoai;

import org.opennars.lab.autoai.structure.NeuralNetworkLayer;
import org.opennars.lab.common.math.DualNumber;
import org.opennars.lab.common.math.DualNumberHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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


        NeuralNetworkLayer layer = new NeuralNetworkLayer();
        layer.neurons = new Neuron[2];

        {
            // count all differentials
            layer.build(context, true);

            context.sizeOfDiff = context.iDiffCounter;
            context.iDiffCounter = 0;

            // "real" building
            layer.build(context, false);
        }

        for(int iteration=0;iteration<250;iteration++) {
            DualNumber[] inputActivation = new DualNumber[3];
            inputActivation[0] = new DualNumber(1.0);
            inputActivation[1] = new DualNumber(0.5);
            inputActivation[2] = new DualNumber(0.4);
            inputActivation[1].diff = new double[context.sizeOfDiff];
            inputActivation[0].diff = new double[context.sizeOfDiff];
            inputActivation[2].diff = new double[context.sizeOfDiff];


            DualNumber[] differences = new DualNumber[2];

            DualNumber[] activations = layer.activate(context, inputActivation);


            DualNumber expectedResult = new DualNumber(0.7);
            expectedResult.diff = new double[context.sizeOfDiff];
            differences[0] = DualNumber.additiveRing(activations[0], expectedResult, -1);

            expectedResult = new DualNumber(0.1);
            expectedResult.diff = new double[context.sizeOfDiff];
            differences[1] = DualNumber.additiveRing(activations[1], expectedResult, -1);


            DualNumber sumOfDifferences = DualNumber.additiveRing(differences[0], differences[1], 1);


            System.out.println("diff=" + Double.toString(sumOfDifferences.real));

            // debug weights
            if (false) {
                System.out.println("weigth[0].=" + Double.toString(layer.neurons[0].weights[0].real));
                System.out.println("weigth[1].=" + Double.toString(layer.neurons[0].weights[1].real));
                System.out.println("weigth[2].=" + Double.toString(layer.neurons[0].weights[2].real));
                System.out.println("bias=" + Double.toString(layer.neurons[0].bias.real));
            }
            double learnRate = 0.03;

            // adapt
            for(int valueIdx=0;valueIdx<context.sizeOfDiff;valueIdx++) {
                context.mapDiffToDualNumber.get(valueIdx).real -= (sumOfDifferences.real * sumOfDifferences.diff[valueIdx] * learnRate);
            }

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

    public static class Layer {
        public Neuron[] neurons;
    }
}
