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
package org.opennars.lab.launcher;

import org.opennars.io.events.EventEmitter.EventObserver;
import org.opennars.io.events.Events;
import org.opennars.main.Nar;
import org.opennars.gui.NARSwing;

/**
 * Game event-loop interface for NARS sensory and motor interaction
 */
abstract public class NARGame implements EventObserver {
    public final Nar nar;
    private int cyclesPerFrame;
    public NARSwing sw;

    public NARGame(Nar nar) {
        this.nar = nar;        
        //if (nar.memory.param.getTiming()!=Memory.Timing.Simulation)
        //    throw new RuntimeException(this + " requires Nar use Simulation timing");
        
        nar.memory.event.on(Events.CyclesEnd.class, this);
        sw=new NARSwing(nar);
    }
    
    abstract public void init();
    abstract public void cycle();
    
    
    public void start(float fps, int cyclesPerFrame) {
        this.cyclesPerFrame = cyclesPerFrame;
        nar.start((long)(1000.0f / fps));
    }
    
    public void stop() {
        nar.stop();
    }


    @Override
    public void event(Class event, Object[] arguments) {
        if (event == Events.CyclesEnd.class) {
            cycle();
        }
    }
    
    
}
