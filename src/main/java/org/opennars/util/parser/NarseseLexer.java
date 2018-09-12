package org.opennars.util.parser;

import java.util.regex.Pattern;

public class NarseseLexer extends Lexer {
    static public final int INTRO = 1;
    static public final int OUTRO = 2;

    static public final int BRACKETOPEN = 3;
    static public final int BRACKETCLOSE = 4;

    static public final int POUNDKEY = 5;

    static public final int SIMILARITY = 6; // <->
    static public final int INHERITANCE = 7; // -->
    static public final int INSTANCE = 8; // {--
    static public final int PROPERTY = 9; // --]
    static public final int INSTANCEPROPERTY = 10; // {-]
    static public final int IMPLICATION = 11; // ==>
    static public final int PREDICTIVEIMPLICATION = 12; // =/>
    static public final int CONCURRENTIMPLICATION = 13; // =|>
    static public final int RETROSPECTIVEIMPLICATION = 14; // =\>
    static public final int EQUIVALENCE = 15; // <=>
    static public final int PREDICTIVEEQUIVALENCE = 16; // </>
    static public final int CONCURRENTEQUIVALENCE = 17; // <|>

    // old style: compound term
    static public final int EXTENSIONALINTERSECTION_OLD = 18; // (&,
    static public final int INTENSIONALINTERSECTION_OLD = 19; // (|,
    static public final int EXTENSIONALDIFFERENCE_OLD = 20; // (-,
    static public final int INTENSIONALDIFFERENCE_OLD = 21; // (~,
    static public final int PRODUCT_OLD = 22; // (*,
    static public final int EXTENSIONALIMAGE_OLD = 23; // (/,
    static public final int INTENSIONALIMAGE_OLD = 24; // (\,
    static public final int NEGATION_OLD = 25; // (--,
    static public final int DISJUNCTION_OLD = 26; // (||,
    static public final int CONJUNCTION_OLD = 27; // (&&,
    static public final int SEQUENTIALEVENTS_OLD = 28; // (&/,
    static public final int PARALLELEVENTS_OLD = 29; // (&|,

    @Override
    protected Token createToken(int ruleIndex, String matchedString) {
        Token token = new Token();

        if (ruleIndex == 1) {
            token.type = Token.OPERATION;
            token.contentOperation = INTRO;
        }
        else if (ruleIndex == 2) {
            token.type = Token.OPERATION;
            token.contentOperation = OUTRO;
        }
        else if (ruleIndex == 3) {
            token.type = Token.OPERATION;
            token.contentOperation = BRACKETOPEN;
        }
        else if (ruleIndex == 4) {
            token.type = Token.OPERATION;
            token.contentOperation = BRACKETCLOSE;
        }
        else if (ruleIndex == 5) {
            token.type = Token.OPERATION;
            token.contentOperation = POUNDKEY;
        }
        else if (ruleIndex == 6) {
            token.type = Token.OPERATION;
            token.contentOperation = SIMILARITY;
        }
        else if (ruleIndex == 7) {
            token.type = Token.OPERATION;
            token.contentOperation = INHERITANCE;
        }

        else if (ruleIndex == 8) {
            token.type = Token.OPERATION;
            token.contentOperation = INSTANCE;
        }
        else if (ruleIndex == 9) {
            token.type = Token.OPERATION;
            token.contentOperation = PROPERTY;
        }
        else if (ruleIndex == 10) {
            token.type = Token.OPERATION;
            token.contentOperation = INSTANCEPROPERTY;
        }
        else if (ruleIndex == 11) {
            token.type = Token.OPERATION;
            token.contentOperation = IMPLICATION;
        }
        else if (ruleIndex == 12) {
            token.type = Token.OPERATION;
            token.contentOperation = PREDICTIVEIMPLICATION;
        }
        else if (ruleIndex == 13) {
            token.type = Token.OPERATION;
            token.contentOperation = CONCURRENTIMPLICATION;
        }
        else if (ruleIndex == 14) {
            token.type = Token.OPERATION;
            token.contentOperation = RETROSPECTIVEIMPLICATION;
        }
        else if (ruleIndex == 15) {
            token.type = Token.OPERATION;
            token.contentOperation = EQUIVALENCE;
        }
        else if (ruleIndex == 16) {
            token.type = Token.OPERATION;
            token.contentOperation = PREDICTIVEEQUIVALENCE;
        }
        else if (ruleIndex == 17) {
            token.type = Token.OPERATION;
            token.contentOperation = CONCURRENTEQUIVALENCE;
        }

        else if (ruleIndex == 18) {
            token.type = Token.OPERATION;
            token.contentOperation = EXTENSIONALINTERSECTION_OLD;
        }
        else if (ruleIndex == 19) {
            token.type = Token.OPERATION;
            token.contentOperation = INTENSIONALINTERSECTION_OLD;
        }
        else if (ruleIndex == 20) {
            token.type = Token.OPERATION;
            token.contentOperation = EXTENSIONALDIFFERENCE_OLD;
        }
        else if (ruleIndex == 21) {
            token.type = Token.OPERATION;
            token.contentOperation = INTENSIONALDIFFERENCE_OLD;
        }
        else if (ruleIndex == 22) {
            token.type = Token.OPERATION;
            token.contentOperation = PRODUCT_OLD;
        }
        else if (ruleIndex == 23) {
            token.type = Token.OPERATION;
            token.contentOperation = EXTENSIONALIMAGE_OLD;
        }
        else if (ruleIndex == 24) {
            token.type = Token.OPERATION;
            token.contentOperation = INTENSIONALIMAGE_OLD;
        }
        else if (ruleIndex == 25) {
            token.type = Token.OPERATION;
            token.contentOperation = NEGATION_OLD;
        }
        else if (ruleIndex == 26) {
            token.type = Token.OPERATION;
            token.contentOperation = DISJUNCTION_OLD;
        }
        else if (ruleIndex == 27) {
            token.type = Token.OPERATION;
            token.contentOperation = CONJUNCTION_OLD;
        }
        else if (ruleIndex == 28) {
            token.type = Token.OPERATION;
            token.contentOperation = SEQUENTIALEVENTS_OLD;
        }
        else if (ruleIndex == 29) {
            token.type = Token.OPERATION;
            token.contentOperation = PARALLELEVENTS_OLD;
        }





        else if (ruleIndex == 18) {
            token.type = Token.IDENTIFIER;
            token.contentString = matchedString;
        }

        /*
        if( ruleIndex == 000 ) {
            token.type = Token.EnumType.OPERATION;
            token.contentOperation = EnumOperationType.BRACEOPEN;
        }
        else if( ruleIndex == 001 ) {
            token.type = Token.EnumType.OPERATION;
            token.contentOperation = EnumOperationType.BRACECLOSE;
        }

        else if( ruleIndex == 009 ) {
            token.type = Token.EnumType.OPERATION;
            token.contentOperation = KEY;
            token.contentString = matchedString;
        }
        else if( ruleIndex == 0010 ) {
            token.type = Token.EnumType.OPERATION;
            token.contentOperation = HALFH;
        }
         */

        return token;
    }

    @Override
    protected void fillRules() {
        tokenRules.add(new Rule(Pattern.compile(("^([ \n\r]+)"))));

        tokenRules.add(new Rule(Pattern.compile(("^(<)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(>)"))));

        tokenRules.add(new Rule(Pattern.compile(("^(\\[)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\])"))));

        tokenRules.add(new Rule(Pattern.compile(("^(#)"))));

        // copula OLD and NEW
        tokenRules.add(new Rule(Pattern.compile(("^(<->)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(-->)"))));

        tokenRules.add(new Rule(Pattern.compile(("^(\\{--)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(--\\])"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\{-\\])"))));
        tokenRules.add(new Rule(Pattern.compile(("^(==>)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(=/>)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(=\\|>)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(=\\\\>)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(<=>)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(</>)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(<\\|>)"))));

        // compound term old
        tokenRules.add(new Rule(Pattern.compile(("^(\\(&,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(\\|,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(-,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(~,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(\\*)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(/,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(\\\\,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(--,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(\\|\\|,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(&&,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(&/,)"))));
        tokenRules.add(new Rule(Pattern.compile(("^(\\(&\\|,)"))));

        tokenRules.add(new Rule(Pattern.compile(("^([a-zA-Z][0-9A-Za-z]*)"))));

        //tokenRules.add(new Rule(Pattern.compile(("^(\\()"))));
        //tokenRules.add(new Rule(Pattern.compile(("^(\\))"))));
        //tokenRules.add(new Rule(Pattern.compile(("^(:[a-zA-Z/\\-\\?!=]+)"))));
        //tokenRules.add(new Rule(Pattern.compile(("^(\\|-)")))); // HALF-H
    }
}
