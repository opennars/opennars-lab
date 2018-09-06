package org.opennars.lab.autoai;

import org.opennars.main.Nar;
import org.opennars.middle.operatorreflection.OperatorReflection;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Executes a macro with a NARS-Reasoner
 */
public class ReasonerMacroExecutive {
    public boolean isFinished;

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, ParseException, IOException, InstantiationException, ParserConfigurationException, SAXException, InvocationTargetException, ClassNotFoundException {
        ReasonerMacroExecutive entry = new ReasonerMacroExecutive();

        String narsese = "";
        narsese += "' weighted sum of all elements - used all over the place for NN's\n";
        narsese += "' with a compact vector representation because the (NARS-term)complexity is a issue here\n";
        narsese += "' we need to add a bias\n";
        narsese += "<(&/, <(*,$1,$2,$3) --> [maddv]>, +1, <(*,$1,bias,$1) --> [add]>, +1) =/> <(*,$1,$2,$3) --> [weighted]>>.\n";
        narsese += "\n";
        narsese += "\n";
        narsese += "' multiply add  $1 = $1 + $2 * $3\n";
        narsese += "< (^madd, {SELF}, $1, $2, $3) =/> <(*, $1, $2, $3) --> [madd] > >.\n";
        narsese += "\n";
        narsese += "' multiply add vector, result is $1, vectors are $2 and $3\n";
        narsese += "< (^maddv, {SELF}, $1, $2, $3) =/> <(*, $1, $2, $3) --> [maddv] > >.\n";
        narsese += "\n";
        narsese += "' add\n";
        narsese += "< (^add, {SELF}, $1, $2, $3) =/> <(*, $1, $2, $3) --> [add]> >.\n";
        narsese += "\n";
        narsese += "\n";
        narsese += "' indirection to call an action which finishes the recording of the macro\n";
        narsese += "<(&/, <(*, res0,  a0, b0) --> [weighted]>, +10, (^fin, {SELF}), +10) =/> <(*, res0,  a0, b0) --> [weighted2]> >.\n";
        narsese += "\n";
        narsese += "' request to generate the code for a weighted sum\n";
        narsese += "<(*, res0, a0, b0) --> [weighted2]>!\n";

        int maximalCycles = 5000;


        // we need a executive to record the operations which are called by the NARS instance
        Executive executive = new Executive();

        entry.entry(executive, narsese, maximalCycles);
    }

    // called by Operator
    public void opFin() {
        isFinished = true;
    }

    public boolean entry(final Executive executive, final String narsese, final int maximalCycles) throws NoSuchMethodException, IllegalAccessException, ParseException, IOException, InstantiationException, SAXException, ParserConfigurationException, InvocationTargetException, ClassNotFoundException {
        Nar reasoner = new Nar();


        OperatorReflection operatorReflection = new OperatorReflection();
        operatorReflection.appendAndRegisterMethodWithoutArguments(
            reasoner,
            "^add",
            executive,
            "opAdd",
            new ArrayList<>(Arrays.asList(new Class[]{String.class, String.class, String.class}))
        );

        operatorReflection.appendAndRegisterMethodWithoutArguments(
            reasoner,
            "^maddv",
            executive,
            "opMaddv",
            new ArrayList<>(Arrays.asList(new Class[]{String.class, String.class, String.class}))
        );

        operatorReflection.appendAndRegisterMethodWithoutArguments(
            reasoner,
            "^madd",
            executive,
            "opMadd",
            new ArrayList<>(Arrays.asList(new Class[]{String.class, String.class, String.class}))
        );

        // operator to finish the processing of the macro
        operatorReflection.appendAndRegisterMethodWithoutArguments(
            reasoner,
            "^fin",
            this,
            "opFin",
            new ArrayList<>(Arrays.asList(new Class[]{}))
        );


        for(String iLine : narsese.split("\n")) {
            reasoner.addInput(iLine);
        }

        // reason util ^fin is called or exit with error if ^fin is not called in a maximal time

        int cycles = 0;
        while(!isFinished) {
            if(cycles > maximalCycles) {
                // something failed
                return false;
            }

            reasoner.cycles(10);
            cycles+=10;

        }

        return true;
    }
}
