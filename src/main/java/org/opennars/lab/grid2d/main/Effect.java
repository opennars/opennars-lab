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
package org.opennars.lab.grid2d.main;


/*
    The result of an action, returned by the game engine to an agent.
*/
public class Effect {
    public final Action action;
    public final String description;
    public final boolean success;
    public final long when;
    
    public Effect(Action a, boolean success, long when, String description) {
        this.when = when;
        this.action = a;
        this.success = success;
        this.description = description;        
    }

    public Effect(Action a, boolean success, long when) {
        this.action = a;
        this.when = when;
        this.success = success;
        this.description = null;
    }

    @Override
    public String toString() {
        String a = action.getClass().getSimpleName() + " " + (success ? "SUCCESS" : "FAIL") + " @" + when;
        if (description!=null)
            a += ": " + description;
        return a;
    }
    
    
    
}
