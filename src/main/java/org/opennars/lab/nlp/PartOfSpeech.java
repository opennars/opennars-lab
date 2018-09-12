package org.opennars.lab.nlp;

import org.opennars.entity.Sentence;
import org.opennars.interfaces.pub.Reasoner;
import org.opennars.io.Narsese;
import org.opennars.io.events.AnswerHandler;
import org.opennars.language.Conjunction;
import org.opennars.main.Nar;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * Component to "parse" part of speech and make sense of (specific) commands in natural language
 */
public class PartOfSpeech {
    public int numberOfCycles = 7000;

    public EnumCommandType commandType = EnumCommandType.NONE;
    public String[] commandArguments;

    public class AnswerHandler extends org.opennars.io.events.AnswerHandler {

        @Override
        public void onSolution(Sentence belief) {
            final Conjunction beliefAsConjuction = (Conjunction)belief.term;

            if( beliefAsConjuction.term[1].toString().equals("make") ) {
                final String commandArgument = beliefAsConjuction.term[2].toString();

                // set command
                commandType = EnumCommandType.MAKE;
                commandArguments = new String[]{commandArgument};
            }



            int here = 5;
        }
    }

    public void parsePartOfSpeech(final String[] tokens) throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException, Narsese.InvalidInputException {
        Reasoner reasoner = new Nar();

        // build narsese from tokens and put it into the reasoner
        final String narseseOfTokens = convTokensToNarsese(tokens);
        reasoner.addInput(narseseOfTokens);

        // ask questions
        {
            // question for command to make something, for example build a NN
            reasoner.ask("(#, BEGIN, make, ?cmd, END)", new AnswerHandler());

            //reasoner.ask("(#, BEGIN, increment, ?cmd, END)?", new AnswerHandler());
            //reasoner.ask("(#, BEGIN, decrement, ?cmd, END)?", new AnswerHandler());

            // TODO< others >
        }


        reasoner.cycles(numberOfCycles);
    }

    /**
     * converts the tokens to a PART based narsese with BEGIN and END terms
     * @param tokens tokens which have to be converted, don't have to be already escaped!
     * @return narsese
     */
    // TODO< move to external nlp utility class >
    private static String convTokensToNarsese(final String[] tokens) {
        StringBuilder strBuilder = new StringBuilder();
        for (final String iToken : tokens) {
            strBuilder.append(iToken.toLowerCase());

            // TODO< this is not safe - make it safe by escaping correctly >
            // append so it can't get assigned important internal term-names, for example the term-name "relation" and so on
            strBuilder.append("_WORD");
            strBuilder.append(",");
        }

        String payloadAsNarsese = strBuilder.toString();
        /// remove last ","
        payloadAsNarsese = payloadAsNarsese.substring(0, payloadAsNarsese.length()-1-1);

        // we add BEGIN and END to indicate the begin and end of a sentence
        // this is necessary for some nlp functionality
        return "(#, BEGIN, " + payloadAsNarsese + ", END).";
    }

    // helper
    public static String[] split(final String text) {
        return text.split("\\ ");
    }

    public enum EnumCommandType {
        NONE, // no command set

        MAKE,
    }
}
