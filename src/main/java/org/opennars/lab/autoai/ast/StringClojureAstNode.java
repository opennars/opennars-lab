package org.opennars.lab.autoai.ast;

import org.opennars.lab.autoai.ast.AbstractClojureAstNode;

public class StringClojureAstNode extends AbstractClojureAstNode {
    public final String name;

    public StringClojureAstNode(final String value) {
        this.name = value;
    }
}
