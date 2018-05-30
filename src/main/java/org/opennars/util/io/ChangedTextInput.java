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
package org.opennars.util.io;

import org.opennars.main.Nar;


/** TextInput subclass that only inputs when the next input value changes from previous */
public class ChangedTextInput  {

    private final Nar nar;
    private String last = null;
    private boolean allowRepeats = false;

    public ChangedTextInput(Nar n) {
        this.nar = n;
    }

 
    
    public boolean set(String s) {
        if (allowRepeats || (last == null) || (!last.equals(s))) {
            nar.addInput(s);
            last = s;
            return true;
        }
        //TODO option to, when else, add with lower budget ?
        return false;
    }

    public void setAllowRepeatInputs(boolean b) {
        this.allowRepeats = b;
    }
}

