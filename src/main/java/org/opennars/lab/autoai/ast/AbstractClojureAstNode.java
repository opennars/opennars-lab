package org.opennars.lab.autoai.ast;

public abstract class AbstractClojureAstNode {
    /** attribute specific to causal-sets - is the id of the node in the causal-set, can be -1 if it has not been initialized */
    public int causalSetNodeId = -1;
}
