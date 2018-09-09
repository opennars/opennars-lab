package org.opennars.lab.autoai.structure;

import org.opennars.lab.common.math.DualNumber;

/**
 * Responsible for backpropagation in Neural Networks with DualNumbers
 */
public class Backpropagation {
    /**
     * do actual backpropagation
     *
     * @param sumOfDifferences difference used for adaptation
     * @param context neural network context for the representation of all weights and settings of the neural network
     */
    public static void backpropagate(final DualNumber sumOfDifferences, NetworkContext context) {
        for(int valueIdx=0;valueIdx<context.sizeOfDiff;valueIdx++) {
            context.mapDiffToDualNumber.get(valueIdx).real -= (sumOfDifferences.real * sumOfDifferences.diff[valueIdx] * context.learnRate);
        }
    }
}
