package org.opennars.lab.autoai.hyperparameteroptimization.nars;

import org.opennars.entity.Sentence;
import org.opennars.entity.TruthValue;
import org.opennars.gui.NARSwing;
import org.opennars.interfaces.pub.Reasoner;
import org.opennars.io.Parser;
import org.opennars.io.events.AnswerHandler;
import org.opennars.lab.autoai.hyperparameteroptimization.ArtifactEvaluator;
import org.opennars.lab.autoai.structure.NeuralNetworkLayer;
import org.opennars.main.Nar;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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


    // we need to store all possible parameters
    public PossibleParametersOfArtifact possibleParametersOfArtifact = new PossibleParametersOfArtifact();

    public Random rng = new Random();

    public void initialize(final int numberOfLayers) {
        truthByConfigurationAndLayer = new HashMap[numberOfLayers];
        for (int i=0;i<numberOfLayers;i++) {
            truthByConfigurationAndLayer[i] = new HashMap<>();
        }
    }

    public void entry() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException, Parser.InvalidInputException {
        reasoner = new Nar();

        NARSwing gui = new NARSwing((Nar)reasoner);


        /// ask questions so we can intercept the changes with a handler

        for (int layerIdx=0; layerIdx<possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer.length; layerIdx++) {
            for (int iPossibleNumberOfNeuronsByLayer : possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer[layerIdx].possibleNumberOfNeurons) {
                reasoner.ask("<{layer" + Integer.toString(layerIdx) + "_" + Integer.toString(iPossibleNumberOfNeuronsByLayer) + "n}-->hyperparameterdb>", new HyperparameterAnswerHandler(layerIdx, iPossibleNumberOfNeuronsByLayer));
            }
        }

        // optimization / reasoning loop
        for(;;) {
            // pick some hyperparameters

            /* we do it by random but we can bias with some kind of metric which takes the truth values
             * and the configuration valus as a distribution and samples it in a randomish fashion
             */
            /// TODO< bias by truth distribution of the already learned trails >

            int[] hyperparameterNumberOfNeurons = new int[possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer.length];

            for(int iLayer=0;iLayer<possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer.length;iLayer++) {
                final PossibleParametersOfArtifact.PossibleNumberOfNeurons possibleNumberOfNeuronsOfLayer = possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer[iLayer];

                final int randomIdxInPossibleArray = rng.nextInt(possibleNumberOfNeuronsOfLayer.possibleNumberOfNeurons.length);

                hyperparameterNumberOfNeurons[iLayer] = possibleNumberOfNeuronsOfLayer.possibleNumberOfNeurons[randomIdxInPossibleArray];
            }


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

        /// relative performance is between 0.0 (terrible) and 1.0 (the best)
        /// 0.1 is a arbitrary scaling and is subject to tuning
        double relativePerformance = 1.0 - evaluationScore * 0.1;
        relativePerformance = Math.max(relativePerformance, 0);


        /// we are using 0.05 for scaling to make it close to 0.5 without loosing the statistical nature of the frequency
        double frequency = 0.5 + (relativePerformance * 2.0 - 1.0) * 0.5 * 0.05;


        for (int layerIdx=0;layerIdx<hyperparameterNumberOfNeurons.length;layerIdx++) {
            final int numberOfNeurons = hyperparameterNumberOfNeurons[layerIdx];


            final String narsese = "<{layer" + Integer.toString(layerIdx) + "_" + Integer.toString(numberOfNeurons) + "n}-->hyperparameterdb>. %" + Double.toString(frequency) + ";0.05%";
            System.out.println(narsese);

            reasoner.addInput(narsese);
        }
    }

    /**
     * evaluate the optimizing artifact
     *
     * @param hyperparameterNumberOfNeurons
     * @return rated metric of network performance vs resources
     */
    protected double evaluateArtifact(final int[] hyperparameterNumberOfNeurons) {
        ArtifactEvaluator evaluator = new ArtifactEvaluator();

        // transfer arguments
        evaluator.layerConfigurations = new ArtifactEvaluator.LayerConfiguration[hyperparameterNumberOfNeurons.length];
        for (int layerIdx=0;layerIdx<hyperparameterNumberOfNeurons.length;layerIdx++) {
            final boolean isLastLayer = layerIdx == hyperparameterNumberOfNeurons.length-1;
            final NeuralNetworkLayer.EnumActivationFunction activationFunctionOflayer = isLastLayer ? NeuralNetworkLayer.EnumActivationFunction.SOFTMAX : NeuralNetworkLayer.EnumActivationFunction.RELU;

            evaluator.layerConfigurations[layerIdx] = new ArtifactEvaluator.LayerConfiguration(activationFunctionOflayer, hyperparameterNumberOfNeurons[layerIdx]);
        }

        return evaluator.evaluate();
    }

    public static void main(String[] args) throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException, Parser.InvalidInputException {
        Demo1 app = new Demo1();

        app.possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer = new PossibleParametersOfArtifact.PossibleNumberOfNeurons[3];
        app.possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer[0] = new PossibleParametersOfArtifact.PossibleNumberOfNeurons();
        app.possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer[0].possibleNumberOfNeurons = new int[]{20, 40, 60};
        app.possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer[1] = new PossibleParametersOfArtifact.PossibleNumberOfNeurons();
        app.possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer[1].possibleNumberOfNeurons = new int[]{20, 15, 10};

        // output layer has two neurons for the test
        app.possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer[2] = new PossibleParametersOfArtifact.PossibleNumberOfNeurons();
        app.possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer[2].possibleNumberOfNeurons = new int[]{2};

        app.initialize(app.possibleParametersOfArtifact.possibleNumberOfNeuronsByLayer.length);

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

    // TODO< refactor to interface to make it artifact type independent, for now we are only handling simple neural-networks
    public static class PossibleParametersOfArtifact {
        public PossibleNumberOfNeurons[] possibleNumberOfNeuronsByLayer;

        public static class PossibleNumberOfNeurons {
            public int[] possibleNumberOfNeurons;
        }
    }
}
