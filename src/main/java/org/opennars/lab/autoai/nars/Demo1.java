package org.opennars.lab.autoai.nars;

import org.opennars.entity.Sentence;
import org.opennars.entity.TruthValue;
import org.opennars.gui.NARSwing;
import org.opennars.interfaces.pub.Reasoner;
import org.opennars.io.Parser;
import org.opennars.io.events.AnswerHandler;
import org.opennars.language.Inheritance;
import org.opennars.language.SetExt;
import org.opennars.main.Nar;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * just a small test where we use NARS (with NARS GUI) to keep track of the knowledge base of the hyperparameter-optimization
 *
 */
public class Demo1 {
    // TODO< structure to keep track of all parameters >
    // we store the direct truth value of each configuration
    Map<Configuration, TruthValue> truthByConfiguration  = new HashMap<>();

    public void entry() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException, Parser.InvalidInputException {
        Reasoner reasoner = new Nar();
        
        reasoner.ask("<{layer0_20n}-->hyperparameterdb>", new HyperparameterAnswerHandler(0, 20));
        reasoner.ask("<{layer0_40n}-->hyperparameterdb>", new HyperparameterAnswerHandler(0, 40));
        reasoner.ask("<{layer0_60n}-->hyperparameterdb>", new HyperparameterAnswerHandler(0, 60));

        reasoner.ask("<{layer1_20n}-->hyperparameterdb>", new HyperparameterAnswerHandler(1, 20));
        reasoner.ask("<{layer1_15n}-->hyperparameterdb>", new HyperparameterAnswerHandler(1, 15));
        reasoner.ask("<{layer1_10n}-->hyperparameterdb>", new HyperparameterAnswerHandler(1, 10));

        NARSwing gui = new NARSwing((Nar)reasoner);

        // testing of answer handler
        reasoner.addInput("<{layer0_20n}-->hyperparameterdb>. %0.5;0.01%");

        // optimization / reasoning loop
        for(;;) {
            // pick some hyperparameters

            // we do it by random but we can bias with some kind of metric which takes the truth values and the configuration valus as a distribution and samples it in a randomish fashion

            int[] hyperparameterNumberOfNeurons = new int[3];

            // we pick it statically for testing
            hyperparameterNumberOfNeurons[0] = 20;
            hyperparameterNumberOfNeurons[1] = 10;
            hyperparameterNumberOfNeurons[2] = 2; // we always need a fixed output


            // evaluate artifact with the hyperparameters
            evaluateArtifact(hyperparameterNumberOfNeurons);




            reasoner.cycles(1);

            int debugMeHere = 5;
        }
    }

    /**
     * evaluate the optimizing artifact
     *
     * @param hyperparameterNumberOfNeurons
     * @return rated metric of network performance vs resources
     */
    protected double evaluateArtifact(final int[] hyperparameterNumberOfNeurons) {

        // we run multiple times and search the run with the lowest
        int nubmerOfTries = 5;

        for (int iTry=0;iTry<nubmerOfTries;iTry++) {

        }
    }

    public static void main(String[] args) throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException, Parser.InvalidInputException {
        Demo1 app = new Demo1();
        app.entry();
    }

    /**
     * Handler to handle answered questions from NARS for changed beliefs about the hyperparameters of the tuned artifact,
     * in this case a contemporary neural-network
     */
    private class HyperparameterAnswerHandler extends AnswerHandler {
        private final int layer;
        private final int numberOfNeurons;

        public HyperparameterAnswerHandler(final int layer, final int numberOfNeurons) {
            this.layer = layer;
            this.numberOfNeurons = numberOfNeurons;
        }

        @Override
        public void onSolution(Sentence belief) {
            final TruthValue truth = belief.truth.clone();

            // update truth by configuration
            truthByConfiguration.put(new Configuration(layer, numberOfNeurons), truth);
        }
    }

    /**
     * configuration of the artifact which is tuned - in this case a contemporary neural-network
     */
    private static class Configuration {
        private final int layer;
        private final int numberOfNeuronsOfLayer;

        public Configuration(final int layer, final int numberOfNeuronsOfLayer) {
            this.layer = layer;
            this.numberOfNeuronsOfLayer = numberOfNeuronsOfLayer;
        }


        @Override
        public int hashCode() {
            int hashcode = layer;

            hashcode ^= numberOfNeuronsOfLayer;

            // commented because we don't store the hyperparameters for all layers per truth value yet
            //for (int iNumberOfNeuronsByLayer : numberOfNeuronsByLayer) {
            //    hashcode ^= iNumberOfNeuronsByLayer;
            //}

            return hashcode;
        }
    }
}
