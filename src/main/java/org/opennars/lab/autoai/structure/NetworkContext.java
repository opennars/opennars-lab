package org.opennars.lab.autoai.structure;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.opennars.lab.common.math.DualNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Keeps track of network global information for backproagation and creation
 */
public class NetworkContext {
    public Random rng = new Random();

    public NormalDistribution centralDistribution = new NormalDistribution();

    /** we need to store which differentiation of any DualNumber is mapped to which value
     * we are using the index of the DualNumberDiff as a array Index
     * */
    public List<DualNumber> mapDiffToDualNumber = new ArrayList<>();

    public int iDiffCounter = 0;

    public int sizeOfDiff = 0;

    public double learnRate = 0;
}
