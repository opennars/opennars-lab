package org.opennars.lab.nlp2;

import org.opennars.language.Term;

/**
 * tuple of narsese for a natural sentence and a coresponding relationship in narsese
 * can contain variables
 */
public class NaturalLanguageRelationshipTuple {
    public final Term relationshipSubject;
    public final Term naturalLanguage;
    public final String relationshipPredicate;

    public NaturalLanguageRelationshipTuple(final Term naturalLanguage, final Term relationshipSubject, final String relationshipPredicate) {
        this.naturalLanguage = naturalLanguage;
        this.relationshipSubject = relationshipSubject;
        this.relationshipPredicate = relationshipPredicate;
    }
}
