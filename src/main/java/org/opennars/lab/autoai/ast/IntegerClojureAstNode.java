package org.opennars.lab.autoai.ast;

public class IntegerClojureAstNode extends NonNameClojureAstNode {
    public final long value;

    public IntegerClojureAstNode(final long value) {
        this.value = value;
    }
}
