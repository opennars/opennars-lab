package org.opennars.lab.nlp2.generators;

/**
 * generates NLP training pairs of a natural sentence and some kind of relationship
 *
 * variable introduction is derived with NARS
 */
public interface PairGenerator {
    /**
     * @return is a next tuple available?
     */
    boolean hasNextTuple();

    /**
     * generates its next training tuple
     * @return training tuple
     */
    NaturalLanguageTrainingTuple generateNextTuple();
}
