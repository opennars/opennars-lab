package org.opennars.lab.nlp2.generators;

/**
 * Pair generator for testing of the program
 */
public class TestPairGenerator implements PairGenerator {
    /** was the test pair already generated? */
    private boolean hasEmitted = false;

    @Override
    public boolean hasNextTuple() {
        return !hasEmitted;
    }

    @Override
    public NaturalLanguageTrainingTuple generateNextTuple() {

        hasEmitted = true;

        return new NaturalLanguageTrainingTuple("that is a small test", "<{(*, small_WORD, test_WORD)}-->relationship>");
    }
}
