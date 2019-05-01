package org.opennars.lab.visionml;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrainLevel1 {
    public static int outputNum = 10;
    public static int nEpochs = 1000;

    public static void main(String[] args) {

        int rngSeed = 43;
        int rngSeed2 = 44;

        int numRows = 64;
        int numColumns = 64;


        int batchSize = 5;

        Random rng2 = new Random(rngSeed2);

        // generate the training data
        DataSetIterator iterator = getTrainingData(batchSize,rng2);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(rngSeed)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .updater(new Adam())
            .l2(1e-4)
            .list()
            .layer(new DenseLayer.Builder()
                .nIn(numRows * numColumns) // Number of input datapoints.
                .nOut(50) // Number of output datapoints.
                .activation(Activation.RELU) // Activation function.
                .weightInit(WeightInit.XAVIER) // Weight initialization.
                .build())
            .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nIn(50)
                .nOut(outputNum)
                .activation(Activation.SOFTMAX)
                .weightInit(WeightInit.XAVIER)
                .build())
            //.pretrain(false).backprop(true) // commented because deprecated
            .build();



        // create the MLN
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();

        // pass a training listener that reports score every 10 iterations
        int eachIterations = 1;
        network.addListeners(new MyScoreIterationListener(eachIterations));

        // fit a dataset for a single epoch
        for( int iEpoch=0; iEpoch<nEpochs; iEpoch++ ){
            iterator.reset();
            network.fit(iterator);
        }

        System.out.println("trained!");


        // fit for multiple epochs
        // val numEpochs = 2
        // network.fit(new MultipleEpochsIterator(numEpochs, emnistTrain))

    }

    private static DataSetIterator getTrainingData(int batchSize, Random rand){
        final Random rng = new Random(44);

        int nSamples = 64*64;

        float MIN_RANGE = -1.0f;
        float MAX_RANGE = 1.0f;

        double [] sum = new double[10];
        double [] input1 = new double[nSamples];
        for (int i= 0; i< nSamples; i++) {
            input1[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
        }

        for (int i= 0; i< sum.length; i++) {
            sum[i] = 0.0;
        }
        sum[0] = 1;

        INDArray inputNDArray1 = Nd4j.create(input1);
        //INDArray inputNDArray2 = Nd4j.create(input2, new int[]{nSamples,1});
        //INDArray inputNDArray = Nd4j.hstack(inputNDArray1,inputNDArray2);
        INDArray outPut = Nd4j.create(sum);
        DataSet dataSet = new org.nd4j.linalg.dataset.DataSet(inputNDArray1, outPut);
        List<org.nd4j.linalg.dataset.DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs, rng);
        return new ListDataSetIterator(listDs,batchSize);

    }

    private static class MyScoreIterationListener extends BaseTrainingListener {
        private final int eachIterations;

        public MyScoreIterationListener(int eachIterations) {
            this.eachIterations = eachIterations;
        }

        public void iterationDone(Model model, int iteration, int epoch) {
            if (iteration % eachIterations == 0) {
                double score = model.score();
                System.out.println("Score at iteration " +Integer.toString(iteration) + " is " + Double.toString(score));
            }
        }
    }
}
