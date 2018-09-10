package org.opennars.lab.autoai.nars;

import org.opennars.entity.Sentence;
import org.opennars.entity.TruthValue;
import org.opennars.gui.NARSwing;
import org.opennars.interfaces.pub.Reasoner;
import org.opennars.io.Parser;
import org.opennars.io.events.AnswerHandler;
import org.opennars.lab.autoai.ArtifactEvaluator;
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
    // we store the direct truth value of each configuration
    // here we store it by layer
    Map<ConfigurationByLayer, TruthValue>[] truthByConfigurationAndLayer = new HashMap[2];

    // commented because we address it by layer
    //Map<Configuration, TruthValue> truthByConfiguration  = new HashMap<>();

    private Reasoner reasoner;

    // last best metric with configuration
    // null implies that there were no trails done ever
    private MetricWithConfiguration bestMetricWithConfiguration = null;

    public void entry() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException, Parser.InvalidInputException {
        reasoner = new Nar();

        NARSwing gui = new NARSwing((Nar)reasoner);


        /// ask questions so we can intercept the changes with a handler
        reasoner.ask("<{layer0_20n}-->hyperparameterdb>", new HyperparameterAnswerHandler(0, 20));
        reasoner.ask("<{layer0_40n}-->hyperparameterdb>", new HyperparameterAnswerHandler(0, 40));
        reasoner.ask("<{layer0_60n}-->hyperparameterdb>", new HyperparameterAnswerHandler(0, 60));

        reasoner.ask("<{layer1_20n}-->hyperparameterdb>", new HyperparameterAnswerHandler(1, 20));
        reasoner.ask("<{layer1_15n}-->hyperparameterdb>", new HyperparameterAnswerHandler(1, 15));
        reasoner.ask("<{layer1_10n}-->hyperparameterdb>", new HyperparameterAnswerHandler(1, 10));


        // testing of answer handler
        reasoner.addInput("<{layer0_20n}-->hyperparameterdb>. %0.5;0.01%");

        // optimization / reasoning loop
        for(;;) {
            // pick some hyperparameters

            /* we do it by random but we can bias with some kind of metric which takes the truth values
             * and the configuration valus as a distribution and samples it in a randomish fashion
             */

            int[] hyperparameterNumberOfNeurons = new int[3];

            /// TODO< pick parameters for artifact which is getting optimized >

            /// we pick it statically for testing
            hyperparameterNumberOfNeurons[0] = 20;
            hyperparameterNumberOfNeurons[1] = 10;
            hyperparameterNumberOfNeurons[2] = 2; // we always need a fixed output


            // evaluate artifact with the hyperparameters
            /// evaluation score: lower is better
            final double evaluationScore = evaluateArtifact(hyperparameterNumberOfNeurons);



            // update NARS knowledge base
            updateNarsKnowledgebaseForEvaluation(hyperparameterNumberOfNeurons, evaluationScore);


            reasoner.cycles(100);

            int debugMeHere = 5;
        }
    }

    /**
     * updates the NARS knowledgebase for a configuration of the artifact under test/optimization
     * @param hyperparameterNumberOfNeurons arguments for the artifact - in this case the number of neurons for the neural-network
     * @param evaluationScore score which the configuration of the artifcat archived - lower is better
     */
    private void updateNarsKnowledgebaseForEvaluation(final int[] hyperparameterNumberOfNeurons, final double evaluationScore) {
        // compute frequency by a metric like function
        double frequency = 0.5;
        // TODO< compute frequency by a metric like function >


        for (int layerIdx=0;layerIdx<hyperparameterNumberOfNeurons.length;layerIdx++) {
            final int numberOfNeurons = hyperparameterNumberOfNeurons[layerIdx];
            reasoner.addInput("<{layer" + Integer.toString(layerIdx) + "_" + Integer.toString(numberOfNeurons) + "n}-->hyperparameterdb>. %" + Double.toString(frequency) + ";0.05%");
        }
    }

    /**
     * evaluate the optimizing artifact
     *
     * @param hyperparameterNumberOfNeurons
     * @return rated metric of network performance vs resources
     */
    protected double evaluateArtifact(final int[] hyperparameterNumberOfNeurons) {
        // TODO< transfer arguments >

        ArtifactEvaluator evaluator = new ArtifactEvaluator();
        return evaluator.evaluate();
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
            truthByConfigurationAndLayer[layer].put(new ConfigurationByLayer(numberOfNeurons), truth);
        }
    }

    /**
     * configuration of the artifact which is tuned - in this case a contemporary neural-network
     */
    private static class Configuration {
        public Map<ConfigurationByLayer, TruthValue>[] truthByConfigurationAndLayer = new HashMap[2];

        public Configuration() {

        }
    }

    /**
     * configuration of the artifact which is tuned - in this case a contemporary neural-network
     * by a single layer
     */
    private static class ConfigurationByLayer {
        private final int numberOfNeuronsOfLayer;

        public ConfigurationByLayer(final int numberOfNeuronsOfLayer) {
            this.numberOfNeuronsOfLayer = numberOfNeuronsOfLayer;
        }


        @Override
        public int hashCode() {
            int hashcode = 0;

            hashcode ^= numberOfNeuronsOfLayer;

            // commented because we don't store the hyperparameters for all layers per truth value yet
            //for (int iNumberOfNeuronsByLayer : numberOfNeuronsByLayer) {
            //    hashcode ^= iNumberOfNeuronsByLayer;
            //}

            return hashcode;
        }
    }

    private static class MetricWithConfiguration {
        public final double metric;
        public final Configuration configuration;

        public MetricWithConfiguration(final double metric, final Configuration configuration) {
            this.metric = metric;
            this.configuration = configuration;
        }
    }
}
