package org.opennars.lab.nlp2;

import org.opennars.entity.Sentence;
import org.opennars.interfaces.pub.Reasoner;
import org.opennars.io.Parser;
import org.opennars.io.events.AnswerHandler;
import org.opennars.lab.nlp.PartOfSpeech;
import org.opennars.language.Term;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** used for applying a sentence to learned rules
 *
 */
public class QueryComponent extends AnswerHandler {

    private Reasoner reasoner;

    /** all answers collected thus far */
    public List<Term> answers = new ArrayList<>();

    public QueryComponent(Reasoner reasoner) {
        this.reasoner = reasoner;
    }

    /**
     * (tries) to apply the relationship-tuples to the a natural language (sentence)
     *
     * @param naturalLanguageRelationshipTuples
     * @param appliedNauralSentence natural language sentence to which the relationshp tuples are tried to be apllied
     */
    public void applyLearnedRulesToNaturalSentence(final List<NaturalLanguageRelationshipTuple> naturalLanguageRelationshipTuples, final String appliedNauralSentence) throws Parser.InvalidInputException {

        /* ex:
         *
         *
         * ' all learned rules
         * <(#, #a, b, c) ==> <{(*, a, c)}-->is>>.
         *
         * ' applied sentence
         * (#, a, b, c).
         *
         * ' questions of all possible relationships
         * <{?}-->is>?
         */


        // tokenize appliedNaturalSentence and convert to narsese
        final String[] tokens = PartOfSpeech.split(appliedNauralSentence);
        final String appliedNaturalSentenceAsNarsese = PartOfSpeech.convTokensToNarsese(tokens);



        reasoner.reset();





        // feed applied sentence into NARS (to boost priorities of words for feeding it with rules)
        reasoner.addInput("' applied sentence");
        reasoner.addInput(appliedNaturalSentenceAsNarsese + ".");



        // feed all relationships as rewrite rules into NARS
        reasoner.addInput("' all learned rules");
        for (final NaturalLanguageRelationshipTuple iRelationshipTuple : naturalLanguageRelationshipTuples) {
            final String narsese = String.format("<%s==><%s-->%s>>.", iRelationshipTuple.naturalLanguage.toString(), iRelationshipTuple.relationshipSubject, iRelationshipTuple.relationshipPredicate);

            // debug
            //System.out.println(narsese);

            reasoner.addInput(narsese);
        }




        // feed applied sentence into NARS
        reasoner.addInput("' applied sentence");
        reasoner.addInput(appliedNaturalSentenceAsNarsese + ".");

        //System.out.println(appliedNaturalSentenceAsNarsese + ".");


        // question of all possible relationships
        reasoner.addInput("' questions of all possible relationships");
        final Set<String> allRelationships = computeAllRelationships(naturalLanguageRelationshipTuples);
        for (final String iRelationship : allRelationships) {
            //System.out.println(String.format("<{?a}-->%s>?", iRelationship));
            //System.out.println(String.format("<{?a, ?b}-->%s>?", iRelationship));
            //System.out.println(String.format("<{?a, ?b, ?c}-->%s>?", iRelationship));

            reasoner.ask(String.format("<{?a}-->%s>", iRelationship), this);
            /// commented because answer handler is not called when the code is active
            /// might be maybe a bug in NARS
            //reasoner.ask(String.format("<{?a,?b}-->%s>", iRelationship), this);
            //reasoner.ask(String.format("<{?a,?b,?c}-->%s>", iRelationship), this);
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

    public void resetAnswers() {
        answers.clear();
    }

    /**
     * answer handler for questions about relationships
     * @param belief
     */
    @Override
    public void onSolution(Sentence belief) {
        answers.add(belief.term.clone());
    }
}
