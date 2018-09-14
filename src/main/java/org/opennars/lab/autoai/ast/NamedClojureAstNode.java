package org.opennars.lab.autoai.ast;

import java.util.List;

/**
 * Abstract Syntax tree node for example for a clojure representation
 *
 */
public class NamedClojureAstNode extends AbstractClojureAstNode {
    /** name of the AST node, clojure ex: (defn x 5 5) then the name is defn */
    public final StringClojureAstNode name;
    public final List<AbstractClojureAstNode> children;

    public NamedClojureAstNode(final StringClojureAstNode name, List<AbstractClojureAstNode> children) {
        this.name = name;
        this.children = children;
    }
}
