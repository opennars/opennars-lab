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
package org.opennars.util.io;

import automenta.vivisect.swing.NWindow;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.opennars.main.Nar;
import org.opennars.gui.NARSwing;
import org.opennars.gui.input.KeyboardInputPanel;
import org.xml.sax.SAXException;

/**
 *
 * @author me
 */
public class KeyboardInputExample {
    
    public static void main(String[] args) {
        //Nar n = Nar.build(new Neuromorphic().realTime());
        //Nar n = Nar.build(new Default().realTime());
        //n.param.duration.set(100);
        
        NARSwing.themeInvert();
        
        Nar n = null;
        try {
            n = new Nar();
        } catch (IOException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(KeyboardInputExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
                
        new NARSwing(n).themeInvert();

        new NWindow("Direct Keyboard Input", new KeyboardInputPanel(n)).show(300, 100, false);
        
        n.start(100);
        
        
    }
}
