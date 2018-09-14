package org.opennars.lab.autoai.causal.representation;

import org.opennars.lab.autoai.ast.AbstractClojureAstNode;
import org.opennars.lab.autoai.ast.IntegerClojureAstNode;
import org.opennars.lab.autoai.ast.NamedClojureAstNode;
import org.opennars.lab.autoai.ast.StringClojureAstNode;

import java.util.*;

/**
 * used to convert Abstract Syntax Tree representation of functional programs (in clojure encoding) as causal-sets
 */
public class ConvertClojureAstToCausalSet {

    /** counter for the number of nodes of the generated causal set */
    private int causalSetNodeCounter;

    /** causal set in the form node->[following nodes] */
    public Map<Integer, List<Integer>> followup = new HashMap<>();


    // public for debugging
    /** uniquely maps AST-nodes for integers to id's of causal-set-nodes */
    public Map<Long, Integer> mapIntegerValueToCausalSetNodeId = new HashMap<>();

    // public for debugging
    /** uniquely maps AST-nodes for integers to id's of causal-set-nodes */
    public Map<String, Integer> mapStringValueToCausalSetNodeId = new HashMap<>();

    /**
     *
     * @param astNode AST-node which has to be converted
     * @param conversationSettings settings for the conversation from the AST to the causal-set
     */
    public int convertAstToCausalSet(final AbstractClojureAstNode astNode, final ConversationSettings conversationSettings) {
        if (astNode instanceof IntegerClojureAstNode) {
            return convertIntegerClojureAstNodeToCausalSet((IntegerClojureAstNode)astNode, conversationSettings);
        }
        else if (astNode instanceof StringClojureAstNode) {
            return convertStringClojureAstNodeToCausalSet((StringClojureAstNode)astNode, conversationSettings);
        }
        else if (astNode instanceof NamedClojureAstNode) {
            return convertNamedClojureAstNodeToCausalSet((NamedClojureAstNode)astNode, conversationSettings);
        }
        else {
            // we are here if we didn't implement the handling of a node!
            // this indicates a bug
            throw new InternalError();
        }
    }

    public int retNumberOfNodes() {
        return causalSetNodeCounter;
    }

    /**
     * converts a Integer-AST-node to a causal-set node
     *
     * @param astNode AST-node which has to be converted
     * @param conversationSettings settings for the conversation from the AST to the causal-set
     */
    private int convertIntegerClojureAstNodeToCausalSet(final IntegerClojureAstNode astNode, final ConversationSettings conversationSettings) {
        int causalSetNodeId;

        if (conversationSettings.createSeperateNodesForIntegerValues) {
            // we don't care about the value of the node if the causal-set-node has to be unique for any value
            causalSetNodeId = retNewCausalSetNodeId();
        }
        else {
            if (mapIntegerValueToCausalSetNodeId.containsKey(astNode.value)) {
                causalSetNodeId = mapIntegerValueToCausalSetNodeId.get(astNode.value);
            }
            else {
                causalSetNodeId = retNewCausalSetNodeId();
                mapIntegerValueToCausalSetNodeId.put(astNode.value, causalSetNodeId);
            }
        }

        astNode.causalSetNodeId = causalSetNodeId;
        return causalSetNodeId;
    }

    /**
     * converts a Integer-AST-node to a causal-set node
     *
     * @param astNode AST-node which has to be converted
     * @param conversationSettings settings for the conversation from the AST to the causal-set
     */
    private int convertStringClojureAstNodeToCausalSet(final StringClojureAstNode astNode, final ConversationSettings conversationSettings) {
        int causalSetNodeId;

        if (conversationSettings.createSeperateNodesForStringValues) {
            // we don't care about the value of the node if the causal-set-node has to be unique for any value
            causalSetNodeId = retNewCausalSetNodeId();
        }
        else {
            if (mapStringValueToCausalSetNodeId.containsKey(astNode.name)) {
                causalSetNodeId = mapStringValueToCausalSetNodeId.get(astNode.name);
            }
            else {
                causalSetNodeId = retNewCausalSetNodeId();
                mapStringValueToCausalSetNodeId.put(astNode.name, causalSetNodeId);
            }
        }

        astNode.causalSetNodeId = causalSetNodeId;
        return causalSetNodeId;
    }

    private int convertNamedClojureAstNodeToCausalSet(final NamedClojureAstNode astNode, final ConversationSettings conversationSettings) {
        // create causal-nodes for all children

        final int causalSetNodeOfName  = convertStringClojureAstNodeToCausalSet(astNode.name, conversationSettings);

        /* TODO< solve how and if we represent the order of the arguments somehow in the
         *       causal-set - this is not necessary for early testing phase but might be crucial for
         *       scaling ths technique to complicated functions and uses
         *     >
         */
        Set<Integer> causalSetNodesOfArguments = new HashSet<>();
        for (final AbstractClojureAstNode iChildren : astNode.children) {
            final int causalSetNodeOfChildren = convertAstToCausalSet(iChildren, conversationSettings);
            causalSetNodesOfArguments.add(causalSetNodeOfChildren);
        }

        // create causal-node for this node
        // TODO< should we introduce a flag and handling to create unique causal-set nodes if the AST-nodes are the same? >
        int causalSetNodeId;
        causalSetNodeId = retNewCausalSetNodeId();

        // build followup DAG where all childrens follow this node
        followup.put(causalSetNodeId, new ArrayList<>());

        followup.get(causalSetNodeId).add(causalSetNodeOfName);

        for (final int iCausalSetNodeOfArgument : causalSetNodesOfArguments) {
            followup.get(causalSetNodeId).add(iCausalSetNodeOfArgument);
        }

        return causalSetNodeId;
    }



    /**
     * generate a new id for the causal set
     * @return generated node id
     */
    private int retNewCausalSetNodeId() {
        return causalSetNodeCounter++;
    }

    public static class ConversationSettings {
        /** must a non-name have a unique node in the causal-set independent on it's value? ex: first 5 has node 0 and send 5 has node 1 -   if the value is false then both nodes are 0 */
        public boolean createSeperateNodesForIntegerValues = false;

        /** must a string (name) have a unique node in the causal-set independent on it' value? */
        public boolean createSeperateNodesForStringValues = false;
    }
}
