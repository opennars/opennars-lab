package org.opennars.lab.nlp2.generators;

/**
 * Tuple for training NLP, is a relationship between a nlp-sentence and a narsese-relationship
 */
public class NaturalLanguageTrainingTuple {
    public final String trainingNlpSentence;
    public final String trainingRelationship;

    public NaturalLanguageTrainingTuple(final String trainingNlpSentence, final String trainingRelationship) {
        this.trainingNlpSentence = trainingNlpSentence;
        this.trainingRelationship = trainingRelationship;
    }
}
