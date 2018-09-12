package org.opennars.lab.nlp2;

/**
 * tuple of narsese for a natural sentence and a coresponding relationship in narsese
 * can contain variables
 */
public class NaturalLanguageRelationshipTuple {
    public final String relationshipNarsese;
    public final String naturalLanguageNarsese;
    public final String relationshipPredicate;

    public NaturalLanguageRelationshipTuple(final String naturalLanguageNarsese, final String relationshipNarsese, final String relationshipPredicate) {
        this.naturalLanguageNarsese = naturalLanguageNarsese;
        this.relationshipNarsese = relationshipNarsese;
        this.relationshipPredicate = relationshipPredicate;
    }
}
