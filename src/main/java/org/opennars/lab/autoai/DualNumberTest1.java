package org.opennars.lab.autoai;

import org.opennars.lab.autoai.structure.Backpropagation;
import org.opennars.lab.autoai.structure.NetworkContext;
import org.opennars.lab.autoai.structure.NeuralNetworkLayer;
import org.opennars.lab.autoai.structure.Neuron;
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
        ArtifactEvaluator evaluator = new ArtifactEvaluator();
        evaluator.evaluate();

        int debugMeHere=5;
    }

}
