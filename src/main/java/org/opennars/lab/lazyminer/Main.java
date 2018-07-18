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
package org.opennars.lab.lazyminer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author patha
 */
public class Main {
    
    public static void main(String args[]) throws IOException, ClassNotFoundException, 
            IllegalAccessException, ParseException, ParserConfigurationException, SAXException, 
            NoSuchMethodException, InstantiationException, InvocationTargetException {
        LazyMiner lm = new LazyMiner();
        PatientSimulator pat = new PatientSimulator(lm);
        for(int i=0;i<1000;i++) {
            lm.cycles(1);
            pat.step();
            lm.HowSensorEventReachedValue("heartrate",110);
            //Thread.sleep(100);
        }
        lm.cycles(1000);
        lm.HowSensorEventReachedValue("heartrate",110);
        lm.cycles(1000);
    }
}
