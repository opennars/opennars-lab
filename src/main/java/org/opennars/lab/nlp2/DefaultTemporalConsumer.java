package org.opennars.lab.nlp2;

import org.opennars.language.Conjunction;
import org.opennars.language.Implication;
import org.opennars.language.Inheritance;
import org.opennars.language.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * Consumer which appends the temporal implication to the knowledge base consisting of NaturalLanguageRelationshipTuple's
 */
public class DefaultTemporalConsumer implements TemporalNlpComponent.TemporalConsumer {
    /** all learned relation ship tuples for NLP till now */
    public List<NaturalLanguageRelationshipTuple> relationshipTuples = new ArrayList<>();

    @Override
    public void consume(Implication temporalImplication) {
        final Inheritance relationship = (Inheritance)temporalImplication.term[1].clone();

        final Term relationshipSubject = relationship.getSubject();
        final String relationshipPredicate = relationship.getPredicate().toString();

        final Conjunction conjuction = (Conjunction)temporalImplication.term[0];

        final Term naturalLanguage = conjuction.term[0].clone();

        relationshipTuples.add(new NaturalLanguageRelationshipTuple(naturalLanguage, relationshipSubject, relationshipPredicate));

        int here = 5;
    }
}
