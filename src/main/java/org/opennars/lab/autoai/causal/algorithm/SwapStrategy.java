package org.opennars.lab.autoai.causal.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SwapStrategy extends AbstractCausalInference {
    public List<Integer> bestLinearization = null;

    private long bestLinearizationEnergy = Long.MAX_VALUE;

    private Random rng = new Random();

    /**
     * tries a new permutation
     * @return is the energy equal or lower?
     */
    public boolean try_() {
        // TODO< generalize swap logic to swappling of subsequences >
		//

        final int swapIdx0 = rngInRangeInclusive(0, bestLinearization.size()-1, rng);

        int swapIdx1;
        while (true) {
            swapIdx1 = rngInRangeInclusive(0, bestLinearization.size()-1, rng);
            if (swapIdx0 != swapIdx1) {
                break;
            }
        }

        currentLinearization = new ArrayList<>(bestLinearization);

        // * swap
        {
            int swapTemp = currentLinearization.get(swapIdx0);
            currentLinearization.set(swapIdx0, currentLinearization.get(swapIdx1));
            currentLinearization.set(swapIdx1, swapTemp);
        }

        // calculate energy
        long energyOfCurrentLinearization;
        try {
            energyOfCurrentLinearization = CausalSetHelpers.calcEnergy(followup, currentLinearization);
        } catch (CausalSetHelpers.NotValid notValid) {
            // is not valid - give up
            return false;
        }

        final boolean lowerEnergy = energyOfCurrentLinearization < bestLinearizationEnergy;

        // check for equality to let it drift
        if (energyOfCurrentLinearization <= bestLinearizationEnergy) {
			//print(currentLinearization)

            bestLinearization = currentLinearization;
            bestLinearizationEnergy = energyOfCurrentLinearization;
        }

        return lowerEnergy;
    }

    /**
     * checks overlap of the ranges
     * @param rangeAParam array of min index and max index
     * @param rangeBParam array of min index and max index
     * @return are the ranges overlapping
     */
    private static boolean checkOverlap(final int[] rangeAParam, final int[] rangeBParam) {
        int[] rangeA = rangeAParam;
        int[] rangeB = rangeBParam;

        if (rangeA[0] > rangeB[0]) {
            // swap
            int[] rangeTemp = rangeA;
            rangeA = rangeB;
            rangeB = rangeTemp;
        }

        return (rangeA[0] <= rangeB[0] && rangeB[0] <= rangeA[0] + rangeA[1]) || (rangeB[0] <= rangeA[0] && rangeA[0] <= rangeB[0] + rangeB[1]);
    }

    private IndexChosenLengthTuple calcRandomSliceIdxWithLength(final int lengthOfArray) {
        final int chosenLength = 1 + rng.nextInt(Math.min(4, lengthOfArray)-1);
        final int idx = rngInRangeInclusive(0, lengthOfArray-chosenLength, rng);
        return new IndexChosenLengthTuple(idx, chosenLength);
    }

    private static class IndexChosenLengthTuple {
        public final int idx;
        public final int chosenLength;

        public IndexChosenLengthTuple(final int idx, final int chosenLength) {
            this.idx = idx;
            this.chosenLength = chosenLength;
        }
    }

    // helper to emulate python's inclusive rng function
    // min <= result <= max
    private static int rngInRangeInclusive(int min, int max, Random rng) {
        int range = max - min;
        return min + rng.nextInt(range+1);
    }
}
