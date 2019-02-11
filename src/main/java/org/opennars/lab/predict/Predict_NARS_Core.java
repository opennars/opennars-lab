/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package org.opennars.lab.predict;

import automenta.vivisect.TreeMLData;
import automenta.vivisect.swing.NWindow;
import automenta.vivisect.swing.PCanvas;
import automenta.vivisect.timeline.BarChart;
import automenta.vivisect.timeline.LineChart;
import automenta.vivisect.timeline.TimelineVis;
import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.opennars.control.DerivationContext;
import org.opennars.entity.Task;
import org.opennars.entity.TruthValue;
import org.opennars.gui.NARSwing;
import org.opennars.io.events.Events.TaskImmediateProcess;
import org.opennars.language.Term;
import org.opennars.main.Nar;
import org.opennars.util.io.ChangedTextInput;
import org.xml.sax.SAXException;

/**
 * https://github.com/encog/encog-java-examples/blob/master/src/main/java/org/encog/examples/neural/predict/sunspot/PredictSunspotElman.java
 * https://github.com/encog/encog-java-examples/blob/master/src/main/java/org/encog/examples/neural/recurrent/elman/ElmanXOR.java
 * @author me
 */
public class Predict_NARS_Core {

    static final Logger LOGGER = Logger.getLogger(Predict_NARS_Core.class.getName());
    static float signal = 0;
    static TreeMLData[] predictions;
    static double maxval = 0;
    static int thinkInterval = 10;
    static HashMap<Integer, TruthValue> pred = new HashMap<Integer, TruthValue>();

    /**
     * Constructor that configures Logger
     */
    public Predict_NARS_Core() {
        Handler consoleHandler;
        if (LOGGER.getHandlers() == null) { // Add handler only if it is not present since LOGGER is Static
            consoleHandler = new ConsoleHandler();
            this.LOGGER.addHandler(consoleHandler);
            consoleHandler.setLevel(Level.INFO);
        }
        LOGGER.setLevel(Level.ALL);
        LOGGER.log(Level.INFO, "{0} Thread is running", Thread.currentThread().getName());
    }

    /**
     * Main method that fires up Predict_NARS_Core class to make predictions
     */
    public void process() throws InterruptedException, IOException, InstantiationException, InvocationTargetException,
            NoSuchMethodException, ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException {
        LOGGER.log(Level.INFO, "{0} Thread is running", Thread.currentThread().getName());
        LOGGER.log(Level.INFO, "Predictions has started ");

        int duration = 10;
        float freq = 1.0f / duration * 0.1f;
        double discretization = 3;
        final Nar n = new Nar();
        n.narParameters.VOLUME = 0;
        n.on(TaskImmediateProcess.class, new TaskImmediateProcess() {
            int curmax = 0;

            @Override
            public void onProcessed(Task t, DerivationContext d) {
                if (t.sentence.getOccurenceTime() > n.time() && t.sentence.truth.getExpectation() > 0.5) {
                    Term term = t.getTerm();
                    int time = (int) t.sentence.getOccurenceTime();
                    int value = -1;
                    String ts = term.toString();
                    if (ts.startsWith("<{x} --> y")) {
                        char cc = ts.charAt("<{x} --> y".length());
                        value = cc - '0';
                        if (time >= curmax) {
                            curmax = time;
                        }
                        maxval = Math.max(maxval, (value) / 10.0);
                        Integer T = time / thinkInterval;
                        boolean add = true;
                        if (pred.containsKey(T)) {
                            add = false;
                        } else {
                            pred.put(T, t.sentence.truth);
                        }
                        if (add) {
                            predictions[0].add(T, (value) / 10.0);
                        }
                    }
                }
            }
        });
        TreeMLData observed = new TreeMLData("value", Color.WHITE).setRange(0, 1f);
        predictions = new TreeMLData[(int) discretization];
        TreeMLData[] reflections = new TreeMLData[(int) discretization];
        for (int i = 0; i < predictions.length; i++) {
            predictions[i] = new TreeMLData("Pred" + i, Color.getHSBColor(0.25f + i / 4f, 0.85f, 0.85f));
            reflections[i] = new TreeMLData("Refl" + i, Color.getHSBColor(0.25f + i / 4f, 0.85f, 0.85f));
            reflections[i].setDefaultValue(0.0);
        }
        TimelineVis tc = new TimelineVis(
                new LineChart(observed).thickness(16f).height(128),
                new LineChart(predictions[0]).thickness(16f).height(128),
                new BarChart(observed).thickness(16f).height(128),
                new BarChart(predictions[0]).thickness(16f).height(128)
        );
        new NWindow("_", new PCanvas(tc)).show(800, 800, true);
        NARSwing.themeInvert();
        new NARSwing(n);
        ChangedTextInput chg = new ChangedTextInput(n);
        while (true) {
            n.cycles(thinkInterval);
            Thread.sleep(30);
            signal = ((float) Math.sin(freq * n.time()) * 0.5f + 0.5f);
            observed.add((int) n.time() / thinkInterval, signal);
            predictions[0].setData(0, maxval);
            int val = (int) (((int) ((signal * discretization)) * (10.0 / discretization)));
            n.addInput("<{x} --> y" + val + ">. :|:");
        }
    }

    public static void main(String[] args) {
        Predict_NARS_Core p = new Predict_NARS_Core();
        try {
            p.process();
        } catch (InterruptedException | IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | ParserConfigurationException | IllegalAccessException | SAXException | ClassNotFoundException | ParseException ex) {
            Logger.getLogger(Predict_NARS_Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
