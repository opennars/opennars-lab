package org.opennars.lab.autoai.structure;

import org.opennars.lab.common.math.DualNumber;

public class Neuron {
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
