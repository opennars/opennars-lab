package org.opennars.lab.nlp2;

import org.opennars.interfaces.pub.Reasoner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** used for applying a sentence to learned rules
 *
 */
public class QueryComponent {

    private Reasoner reasoner;

    public List<String> relationships = new ArrayList<>();

    public void applyLearnedRulesToNaturalSentence(final List<NaturalLanguageRelationshipTuple> naturalLanguageRelationshipTuples, final String appliedNauralSentence) {
         /* ex:
     *
     *
' all learned rules
<(#, #a, b, c) ==> <{(*, a, c)}-->is>>.

' applied sentence
(#, a, b, c).

' questions of all possible relationships
<{?}-->is>?
     */


        reasoner.reset();

        // feed all relationships as rewrite rules into NARS
        reasoner.addInput("' all learned rules");
        for (final NaturalLanguageRelationshipTuple iRelationshipTuple : naturalLanguageRelationshipTuples) {
            reasoner.addInput(String.format("<%s==>%s>.", iRelationshipTuple.naturalLanguageNarsese, iRelationshipTuple.relationshipNarsese));
        }


        // TODO< tokenize appliedNauralSentence and convert to narsese >
        final String appliedNaturalSentenceAsNarsese = null;

        // feed applied sentence into NARS
        reasoner.addInput("' applied sentence");
        reasoner.addInput(appliedNaturalSentenceAsNarsese + ".");


        // question of all possible relationships
        reasoner.addInput("' questions of all possible relationships");
        final Set<String> allRelationships = computeAllRelationships(naturalLanguageRelationshipTuples);
        for (final String iRelationship : allRelationships) {
            // TODO< ask questions with callback!!!!! >

            reasoner.addInput(String.format("<{?}-->%s>?", iRelationship));
            reasoner.addInput(String.format("<{?,?}-->%s>?", iRelationship));
            reasoner.addInput(String.format("<{?,?,?}-->%s>?", iRelationship));
        }

        // give the reasoner some time
        reasoner.cycles(7000);
    }

    private static Set<String> computeAllRelationships(final List<NaturalLanguageRelationshipTuple> naturalLanguageRelationshipTuples) {
        Set<String> relationships = new HashSet<>();
        for (final NaturalLanguageRelationshipTuple iTuple : naturalLanguageRelationshipTuples) {
            relationships.add(iTuple.relationshipPredicate);
        }
        return relationships;
    }
}
