package org.opennars.util.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParser {
    private Token currentToken;

    protected List<Arc> arcs = new ArrayList<>();
    public  Lexer lexer;

    private List<Line> lines = new ArrayList<>();
    private int currentLineNumber = 0;

    public AbstractParser() {
        this.fillArcs();

        this.lines.add(new Line());
    }

    /** \brief get called from the constructor to fill the Arc Tables required for parsing
     *
     */
    abstract protected void fillArcs();

    /** \brief gets called before the actual parsing
     *
     */
    abstract protected void setupBeforeParsing();

    /**
     *
     * @param arcTableIndex is the index in the ArcTable
     * @return
     */
    // NOTE< this is written recursive because it is better understandable that way and i was too lazy to reformulate it >
    final private EnumRecursionReturn parseRecursive(int arcTableIndex) {
        EnumRecursionReturn returnValue;

        boolean ateAnyToken = false;

        for(;;) {
            System.out.println("arcTableIndex=" + Integer.toString(arcTableIndex));

            switch( arcs.get(arcTableIndex).type ) {
                ///// NIL
                case NIL:
                // if the alternative is null we just go to next, if it is not null we follow the alternative
                // we do this to simplify later rewriting of the rule(s)
                if (arcs.get(arcTableIndex).alternative == null) {
                    returnValue = EnumRecursionReturn.OK;
                }
                else {
                    returnValue = EnumRecursionReturn.BACKTRACK;
                }
                break;

                ///// OPERATION
                case OPERATION:
                if (currentToken.type == Token.OPERATION && arcs.get(arcTableIndex).info == currentToken.contentOperation) {
                    returnValue = EnumRecursionReturn.OK;
                }
                else {
                    returnValue = EnumRecursionReturn.BACKTRACK;
                }
                break;

                ///// TOKEN
                case TOKEN:
                if (arcs.get(arcTableIndex).info == currentToken.type) {
                    returnValue = EnumRecursionReturn.OK;
                }
                else {
                    returnValue = EnumRecursionReturn.BACKTRACK;
                }
                break;

                ///// ARC
                case ARC:
                returnValue = parseRecursive(arcs.get(arcTableIndex).info);
                break;

                ///// END
                case END:

                // TODO< check if we really are at the end of all tokens >

                //System.out.println("end");

                return EnumRecursionReturn.OK;



                default:
                throw new InternalError();
            }


            if (returnValue == EnumRecursionReturn.OK) {
                arcs.get(arcTableIndex).callback.call(this, currentToken);
                returnValue = EnumRecursionReturn.OK;
            }

            if (returnValue == EnumRecursionReturn.BACKTRACK) {
                // we try alternative arcs
                //System.out.println("backtracking");

                if (arcs.get(arcTableIndex).alternative != null) {
                    arcTableIndex = arcs.get(arcTableIndex).alternative;
                }
                else if (ateAnyToken) {
                    throw new InternalError();
                }
                else {
                    return EnumRecursionReturn.BACKTRACK;
                }
            }
            else {
                // accept the token

                if(
                    arcs.get(arcTableIndex).type == AbstractParser.Arc.EnumType.OPERATION ||
                    arcs.get(arcTableIndex).type == AbstractParser.Arc.EnumType.TOKEN
                ) {
                    //System.out.println("eat token");

                    eatToken();
                    ateAnyToken = true;
                }

                arcTableIndex = arcs.get(arcTableIndex).next;
            }
        }
    }

    /**
     * do the parsing
     *
     */
    final public void parse() {
        this.currentToken = new Token();

        this.setupBeforeParsing();

        // read first token
        eatToken();

        //currentToken.debugIt();

        final EnumRecursionReturn recursionReturn = this.parseRecursive(0);

        if (recursionReturn == EnumRecursionReturn.BACKTRACK) {
            throw new InternalError();
        }

        // check if the last token was an EOF
        if (currentToken.type != Token.EOF) {
            // TODO< add line information and marker >

            // TODO< get the string format of the last token >
            throw new InternalError("Unexpected Tokens after (Last) Token");
        }
    }

    private void eatToken() {
        final Lexer.EnumLexerCode lexerReturnValue = lexer.nextToken();

        final boolean success = lexerReturnValue == Lexer.EnumLexerCode.OK;
        if (!success) {
            throw new InternalError("Could not consume token!");
        }

        this.currentToken = lexer.retCurrentToken();

        //currentToken.debugIt();

        this.addTokenToLines(this.currentToken.copy());
    }

    final public void setLexer(final Lexer lexer) {
        this.lexer = lexer;
    }

    final public void addTokenToLines(final Token token) {
        if (token.line != this.currentLineNumber) {
            currentLineNumber = token.line;
            lines.add(new Line());
        }

        lines.get(lines.size()-1).tokens.add(token);
    }



    private enum EnumRecursionReturn {
        //ERROR, // if some error happened, will be found in ErrorMessage
        OK,
        BACKTRACK // if backtracking should be used from the caller
    }

    public interface Callback {
        void call(AbstractParser parser, final Token currentToken);
    }

    public static class Arc {
        public final EnumType type;
        public final Callback callback;
        public final int next;
        public final Integer alternative; // nullable

        /** Token Type, Operation Type and so on */
        public final int info;

        public Arc(final EnumType type, final int info, final Callback callback, final int next, final Integer alternative) {
            this.type        = type;
            this.info        = info;
            this.callback    = callback;
            this.next        = next;
            this.alternative = alternative;
        }

        public enum EnumType {
            TOKEN,
            OPERATION,
            ARC,        // another arc, info is the index of the start
            KEYWORD,    // Info is the id of the Keyword

            END,        // Arc end
            NIL,        // Nil Arc

            ERROR       // not used Arc
        }
    }

}
