package org.opennars.lab.autoai.causal;

import org.opennars.lab.autoai.ast.AbstractClojureAstNode;
import org.opennars.lab.autoai.ast.IntegerClojureAstNode;
import org.opennars.lab.autoai.ast.NamedClojureAstNode;
import org.opennars.lab.autoai.ast.StringClojureAstNode;
import org.opennars.lab.autoai.causal.algorithm.CausalInference;
import org.opennars.lab.autoai.causal.algorithm.CausalSetHelpers;
import org.opennars.lab.autoai.causal.algorithm.SwapStrategy;
import org.opennars.lab.autoai.causal.representation.ConvertClojureAstToCausalSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Test to test the functionality of the causal-set to minimize the energy/entropy of a Clojure-like AST "program"
 *
 * we need this functionality for the automated code transformations and refactoring of auto-ai
 */
public class TestComputeEnergyAndLinearizationOfAst {
    public static void main(String[] args) throws CausalSetHelpers.NotValid {
        // create AST for testing

        AbstractClojureAstNode rootAstNode;

        // TODO< more complicated AST >

        IntegerClojureAstNode int5 = new IntegerClojureAstNode(5);
        IntegerClojureAstNode int6 = new IntegerClojureAstNode(6);

        StringClojureAstNode string0 = new StringClojureAstNode("+");
        StringClojureAstNode string1 = new StringClojureAstNode("+");

        List<AbstractClojureAstNode> named0Children = new ArrayList<>();
        named0Children.add(int5);
        named0Children.add(int6);

        NamedClojureAstNode named0 = new NamedClojureAstNode(string0, named0Children);


        List<AbstractClojureAstNode> named1Children = new ArrayList<>();
        named1Children.add(int5);
        named1Children.add(named0);
        named1Children.add(int6);

        NamedClojureAstNode named1 = new NamedClojureAstNode(string1, named1Children);

        rootAstNode = named1;


        // convert AST to equivalent causal-set
        ConvertClojureAstToCausalSet.ConversationSettings conversationSettings = new ConvertClojureAstToCausalSet.ConversationSettings();

        ConvertClojureAstToCausalSet convert = new ConvertClojureAstToCausalSet();
        convert.convertAstToCausalSet(rootAstNode, conversationSettings);


        // find minimal loss/energy/entropy and linearization of causal-set
        // TODO< >
        minimizeLossEnergy(convert.followup, convert.retNumberOfNodes());


        // print result
        // TODO< >

        int debugMeHere = 5;

    }

    private static void minimizeLossEnergy(final Map<Integer, List<Integer>> followup, final int numberOfNodes) throws CausalSetHelpers.NotValid {
        CausalInference inference = new CausalInference();

        inference.numberOfNodes = numberOfNodes;
        inference.followup = followup;

        long minimalEnergy = Long.MAX_VALUE;

        for (long iteration = 0; iteration<1024; iteration++) {
            inference.reset();
            inference.build();

            while(true) {
                final boolean terminate = inference.step();
                if (terminate) {
                    break;
                }
            }

            final int currentEnergy = inference.calcEnergy(inference.currentLinearization);

            if (currentEnergy < minimalEnergy) {
                minimalEnergy = currentEnergy;

                System.out.println(String.format("e=%s   linearization=%s", currentEnergy, inference.currentLinearization));
            }

        }


        SwapStrategy swapStrategy = new SwapStrategy();
        swapStrategy.followup = inference.followup;
        swapStrategy.bestLinearization = new ArrayList<>(inference.currentLinearization);
        long bestLinearizationEnergy = inference.calcEnergy(inference.currentLinearization);

        long softTimeout = 500000;

        long currentSoftTimeout = softTimeout;

        for (long iteration=0; iteration<8000000; iteration++) {
            softTimeout--;
            if (softTimeout <= 0) {
                System.out.println("v: soft timeout");
                break;
            }

            final boolean foundLowerEnergeticConfiguration = swapStrategy.try_();
            if (foundLowerEnergeticConfiguration) {
                currentSoftTimeout = softTimeout; // reset soft timeout

                System.out.println(String.format("e=%s", bestLinearizationEnergy));
            }
        }
    }
}
