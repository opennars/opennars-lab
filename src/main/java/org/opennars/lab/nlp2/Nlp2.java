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
 * experiment for nlp with multiple Reasoners which have different tasks
 */
public class Nlp2 {
    /* commented to keep track of how to use it
    public void entry() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException {
        // TODO< build reasoner component which task it is to generate variations of a sentence with the machinery of PART >

        // test the temporal component
        TemporalNlpComponent temporalComponent = new TemporalNlpComponent();
        temporalComponent.initalize();
        temporalComponent.processPair("(#, a, b, c)", "<{a, c}-->is>");
    }

    public static void main(String[] args) throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException {
        Nlp2 nlp2 = new Nlp2();
        nlp2.entry();
    }
    */

    // helper
    // TODO< pull out >
    public static boolean containsSelf(final Term term) {
        // simple method which just looks for SELF - relativly easy to break
        return ((String)term.name()).contains("SELF");
    }
}
