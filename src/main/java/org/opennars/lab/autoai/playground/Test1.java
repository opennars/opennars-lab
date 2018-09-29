package org.opennars.lab.autoai.playground;

import org.opennars.lab.autoai.hyperparameteroptimization.ArtifactEvaluator;
import org.opennars.lab.autoai.structure.NeuralNetworkLayer;
import org.opennars.lab.common.math.DualNumber;

import java.util.ArrayList;
import java.util.List;

public class Test1 {
    public static void main(String[] args) {
        ArtifactEvaluator evaluator = new ArtifactEvaluator();
        evaluator.numberOfInputs = 4;

        evaluator.layerConfigurations = new ArtifactEvaluator.LayerConfiguration[2];
        evaluator.layerConfigurations[0] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.RELU, 8);
        evaluator.layerConfigurations[1] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.RELU, 3);

        evaluator.outputsConfigurations = new ArtifactEvaluator.LayerConfiguration[4*2];
        evaluator.outputsConfigurations[0] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.SOFTMAX, 10); // instruction #0
        evaluator.outputsConfigurations[1] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.SOFTMAX, 7);
        evaluator.outputsConfigurations[2] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.SOFTMAX, 7);
        evaluator.outputsConfigurations[3] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.SOFTMAX, 7);

        evaluator.outputsConfigurations[4] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.SOFTMAX, 10); // instruction #0
        evaluator.outputsConfigurations[5] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.SOFTMAX, 7);
        evaluator.outputsConfigurations[6] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.SOFTMAX, 7);
        evaluator.outputsConfigurations[7] = new ArtifactEvaluator.LayerConfiguration(NeuralNetworkLayer.EnumActivationFunction.SOFTMAX, 7);

        List<InputWithCategories> inputWithCategories = new ArrayList<>();

        // input : math-add, math-mul, stack-add, stack-op

        // instruction:
        // 0 : add
        // 1 : mul
        // 2 : sub
        // 3 : nop
        // 4 : mov stack, X

        // registers : 5 : stack pointer
        // registers : 6 : constant 1.0

        inputWithCategories.add(new InputWithCategories(new double[]{1,0,0,0}, new int[]{0,0,0,0, 3,0,0,0}));
        inputWithCategories.add(new InputWithCategories(new double[]{1,0,0,0}, new int[]{0,0,1,0, 3,0,0,0}));
        inputWithCategories.add(new InputWithCategories(new double[]{1,0,0,0}, new int[]{0,0,0,1, 3,0,0,0}));
        inputWithCategories.add(new InputWithCategories(new double[]{1,0,0,0}, new int[]{0,0,1,1, 3,0,0,0}));

        inputWithCategories.add(new InputWithCategories(new double[]{0,1,0,0}, new int[]{1,0,1,1, 3,0,0,0}));
        inputWithCategories.add(new InputWithCategories(new double[]{0,1,0,0}, new int[]{1,0,0,0, 3,0,0,0}));

        // stack pop
        inputWithCategories.add(new InputWithCategories(new double[]{0,0,0,1}, new int[]{2,5,5,6, 3,0,0,0}));

        // stack push register 0
        inputWithCategories.add(new InputWithCategories(new double[]{0,0,0,1}, new int[]{4,0,0,0, 0,5,5,6}));


        for(InputWithCategories i : inputWithCategories) {
            ArtifactEvaluator.TrainingSample trainingSample0 = new ArtifactEvaluator.TrainingSample();
            // math-add, math-mul, stack-add, stack-op
            trainingSample0.input = i.input;
            trainingSample0.output = new ArtifactEvaluator.TrainingSample.ExpectedOutput[4*2];
            trainingSample0.output[0] = new ArtifactEvaluator.TrainingSample.ExpectedSoftmaxOutput(i.categories[0]);
            trainingSample0.output[1] = new ArtifactEvaluator.TrainingSample.ExpectedSoftmaxOutput(i.categories[1]);
            trainingSample0.output[2] = new ArtifactEvaluator.TrainingSample.ExpectedSoftmaxOutput(i.categories[2]);
            trainingSample0.output[3] = new ArtifactEvaluator.TrainingSample.ExpectedSoftmaxOutput(i.categories[3]);

            trainingSample0.output[4] = new ArtifactEvaluator.TrainingSample.ExpectedSoftmaxOutput(i.categories[4]);
            trainingSample0.output[5] = new ArtifactEvaluator.TrainingSample.ExpectedSoftmaxOutput(i.categories[5]);
            trainingSample0.output[6] = new ArtifactEvaluator.TrainingSample.ExpectedSoftmaxOutput(i.categories[6]);
            trainingSample0.output[7] = new ArtifactEvaluator.TrainingSample.ExpectedSoftmaxOutput(i.categories[7]);



            evaluator.trainingSamples.add(trainingSample0);
        }


        evaluator.evaluate();


        DualNumber[][] activations = evaluator.forwardPropagate(new double[]{1,0,0,0});

        int debugMeHere = 5;
    }

    // used for training a classifier with multiple softmax outputs
    static class InputWithCategories {
        public final int[] categories;
        public final double[] input;

        public InputWithCategories(final double[] input, final int[] categories) {
            this.input = input;
            this.categories = categories;
        }
    }
}
