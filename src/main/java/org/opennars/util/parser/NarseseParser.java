package org.opennars.util.parser;

/**
 * parser to parse Narsese
 */
public class NarseseParser extends AbstractParser {
    /**
     * Parsing action callback which doesn't do anything
     */
    public class NothingCallback implements AbstractParser.Callback {
        @Override
        public void call(AbstractParser parser, Token currentToken) {
        }
    }

    /**
     * Parsing action callback for the begin of a statement
     */
    public class StatementBeginCallback implements AbstractParser.Callback {
        @Override
        public void call(AbstractParser parser, Token currentToken) {
            // TODO
        }
    }

    /**
     * Parsing action callback for the end of a statement
     */
    public class StatementEndCallback implements AbstractParser.Callback {
        @Override
        public void call(AbstractParser parser, Token currentToken) {
            // TODO
        }
    }

    /**
     * Set copula of statement
     */
    public class StatementSetCopulaCallback implements AbstractParser.Callback {
        @Override
        public void call(AbstractParser parserArgument, Token currentToken) {
            // currentToken.contentOperation // TODO
        }
    }

    /**
     * setup compound and set type
     */
    public class CompoundSetupAndSetTypeCallback implements AbstractParser.Callback {
        @Override
        public void call(AbstractParser parser, Token currentToken) {
            // currentToken.contentOperation // TODO
        }
    }

    @Override
    protected void fillArcs() {
        final int TODO = -1; // for developing the grammar - not required if fully developed


        /*   0 */this.arcs.add(new Arc(Arc.EnumType.ARC, 5, new NothingCallback(), 1, null));
        /*   1 */this.arcs.add(new Arc(Arc.EnumType.END, 0, new NothingCallback(), 0, 0));
        /*   2 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*   3 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*   4 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));

        // ARC
        // we are here if we expect another (compound) term or a sentence
        /*   5 */this.arcs.add(new Arc(AbstractParser.Arc.EnumType.TOKEN , Token.IDENTIFIER, new NothingCallback() , TODO, 6));
        /*   6 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.INTRO, new StatementBeginCallback() , 7, 60));

        //   jump into statement arc
        /*   7 */this.arcs.add(new Arc(Arc.EnumType.ARC, 15, new NothingCallback(), TODO, null));
        /*   8 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*   9 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));


        /*  10 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  11 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  12 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  13 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  14 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));

        // statement
        //  statement arc has begun
        /*  15 */this.arcs.add(new Arc(Arc.EnumType.ARC, 5, new NothingCallback(), 16, null));
        /*  16 */this.arcs.add(new Arc(Arc.EnumType.NIL, 0, new NothingCallback(), 24, null));
        /*  17 */this.arcs.add(new Arc(Arc.EnumType.ARC, 5, new NothingCallback(), 18, 0));
        /*  18 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.OUTRO, new StatementEndCallback() , 19, null));
        /*  19 */this.arcs.add(new Arc(Arc.EnumType.END, 0, new NothingCallback(), 0, 0));


        /*  20 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  21 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  22 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  23 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));

        // all copula of statement
        /*  24 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.INHERITANCE, new StatementSetCopulaCallback() , 17, 25));
        /*  25 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.SIMILARITY, new StatementSetCopulaCallback() , 17, 26));
        /*  26 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.INSTANCE, new StatementSetCopulaCallback(), 17, 27));
        /*  27 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.PROPERTY, new StatementSetCopulaCallback(), 17, 28));
        /*  28 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.INSTANCEPROPERTY, new StatementSetCopulaCallback(), 17, 29));
        /*  29 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.IMPLICATION, new StatementSetCopulaCallback(), 17, 30));

        /*  30 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.PREDICTIVEIMPLICATION, new StatementSetCopulaCallback(), 17, 31));
        /*  31 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.CONCURRENTIMPLICATION, new StatementSetCopulaCallback(), 17, 32));
        /*  32 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.RETROSPECTIVEIMPLICATION, new StatementSetCopulaCallback(), 17, 33));
        /*  33 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.EQUIVALENCE, new StatementSetCopulaCallback(), 17, 34));
        /*  34 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.PREDICTIVEEQUIVALENCE, new StatementSetCopulaCallback(), 17, 35));
        /*  35 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.CONCURRENTEQUIVALENCE, new StatementSetCopulaCallback(), 17, null));
        /*  36 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  37 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  38 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  39 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));


        /*  40 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  41 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  42 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  43 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  44 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));

        /*  45 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  46 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  47 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  48 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  49 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));

        // compound term
        /*  50 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  51 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  52 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  53 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  54 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));

        /*  55 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  56 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  57 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  58 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));
        /*  59 */this.arcs.add(new Arc(Arc.EnumType.ERROR, 0, new NothingCallback(), 0, 0));

        // compound term type
        /*  60 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.EXTENSIONALINTERSECTION_OLD, new CompoundSetupAndSetTypeCallback() , 72, 61));
        /*  61 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.INTENSIONALINTERSECTION_OLD, new CompoundSetupAndSetTypeCallback() , 72, 62));
        /*  62 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.EXTENSIONALDIFFERENCE_OLD, new CompoundSetupAndSetTypeCallback() , 72, 63));
        /*  63 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.INTENSIONALDIFFERENCE_OLD, new CompoundSetupAndSetTypeCallback() , 72, 64));
        /*  64 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.PRODUCT_OLD, new CompoundSetupAndSetTypeCallback() , 72, 65));
        /*  65 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.EXTENSIONALIMAGE_OLD, new CompoundSetupAndSetTypeCallback() , 72, 66));
        /*  66 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.INTENSIONALIMAGE_OLD, new CompoundSetupAndSetTypeCallback() , 72, 67));
        /*  67 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.NEGATION_OLD, new CompoundSetupAndSetTypeCallback() , 72, 68));
        /*  68 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.DISJUNCTION_OLD, new CompoundSetupAndSetTypeCallback() , 72, 69));
        /*  69 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.CONJUNCTION_OLD, new CompoundSetupAndSetTypeCallback() , 72, 70));
        /*  70 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.SEQUENTIALEVENTS_OLD, new CompoundSetupAndSetTypeCallback() , 72, 71));
        /*  71 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.PARALLELEVENTS_OLD, new CompoundSetupAndSetTypeCallback() , 72, null));

        // open arc for compound term
        /*  72 */this.arcs.add(new Arc(Arc.EnumType.ARC, TODO, new NothingCallback(), TODO, null));






        // OLD CODE just to remember how to do stuff

        //
        ///*   1 */
        //
        ///*   0 */this.arcs.add(new Arc(Arc.EnumType.OPERATION , NarseseLexer.INTRO, new StatementBeginCallback() , 2, 1));
        //
        //// if it was not a "<" then we expect a compound term, this jumps to the handling of the compound term
        ///*   1 */this.arcs.add(new Arc(Arc.EnumType.NIL , 0, new NothingCallback() , 50, 1));
        //
        ///*   3 */this.arcs.add(new Arc(AbstractParser.Arc.EnumType.END      , 0                                                    , new NothingCallback(),0, null                   ));


    }

    @Override
    protected void setupBeforeParsing() {
    }
}
