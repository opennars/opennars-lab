package org.opennars.lab.nlp2;

import org.opennars.entity.Task;
import org.opennars.interfaces.pub.Reasoner;
import org.opennars.io.events.EventEmitter;
import org.opennars.io.events.Events;
import org.opennars.language.Implication;
import org.opennars.language.Term;
import org.opennars.main.Nar;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * Component which task it is to come up with temporal relationships between the sentence parts which are passed into the reasoner
 */
public class TemporalNlpComponent implements EventEmitter.EventObserver {
    private final TemporalConsumer temporalConsumer;

    public TemporalNlpComponent(final TemporalConsumer temporalConsumer) {
        this.temporalConsumer = temporalConsumer;
    }

    public void initalize() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException {
        reasoner = new Nar();

        reasoner.event(this, true, Events.TaskDerive.class);
    }

    /**
     * processes a pair of a nlp sentence and a coresponding relationship
     *
     * ex:
     * nlpRepresentation   (#, a, b, c)
     * relationship        <{a, c}-->is>
     *
     * @param nlpRepresentation nlp representation of the to be learned relationship, might be most of the time PART
     * @param relationship some kind of relationship as narsese
     */
    public void processPair(final String nlpRepresentation, final String relationship) {
        reasoner.addInput(nlpRepresentation + ". :|:");
        reasoner.cycles(5);
        reasoner.addInput(relationship + ". :|:");

        // give it enough time to derive enough conclusions
        reasoner.cycles(1500);

        // we need to reset it to have a fresh mind for the processing of the next relationship between a sentence and a fact
        reasoner.reset();
    }

    Reasoner reasoner;

    @Override
    public void event(Class event, Object[] args) {
        if (event.equals(Events.TaskDerive.class)) {
            final Task task = (Task)args[0];
            final boolean wasRevised = (boolean)args[1];
            final boolean wasSingle = (boolean)args[2];

            final Term rootTerm = task.sentence.term;

            // filter for anything related to SELF because we are not interested in this
            if (Nlp2.containsSelf(rootTerm)) {
                return;
            }

            if (!(rootTerm instanceof Implication)) {
                return;
            }

            final Implication rootTermAsImplication = (Implication)rootTerm;

            if(rootTermAsImplication.getTemporalOrder() == 0) {
                return;
            }

            // TODO< rename after NARS theory >
            Term condition;
            Term conclusion;

            if (rootTerm.getTemporalOrder() == 1) {
                condition = rootTermAsImplication.term[0];
                conclusion = rootTermAsImplication.term[1];
            }
            else {
                condition = rootTermAsImplication.term[1];
                conclusion = rootTermAsImplication.term[0];
            }

            // build temporal implication "in the right order"
            Implication buildImplication = Implication.make(condition, conclusion);

            // feed to to the mechanism which is responsible for working with the temporal relationships
            temporalConsumer.consume(buildImplication);

            int here = 5;
        }
    }

    /**
     * consumer which works with temporal statement for NLP processing
     */
    public interface TemporalConsumer {
        void consume(final Implication temporalImplication);
    }
}
