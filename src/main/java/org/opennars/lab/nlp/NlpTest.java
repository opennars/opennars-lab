package org.opennars.lab.nlp;

import org.opennars.io.Narsese;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public class NlpTest {
    public static void main(String[] args) throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException, Narsese.InvalidInputException {
        NlpGui gui = new NlpGui();
        gui.setup();


        for(;;) {

        }
    }
}
