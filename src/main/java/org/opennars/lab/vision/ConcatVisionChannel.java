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
package org.opennars.lab.vision;

import java.util.ArrayList;
import java.util.List;
import org.opennars.main.Nar;
import org.opennars.control.DerivationContext;
import org.opennars.entity.Task;
import org.opennars.interfaces.Timable;
import org.opennars.language.Term;
import org.opennars.plugin.perception.SensoryChannel;
import org.opennars.storage.Buffer;

public class ConcatVisionChannel extends SensoryChannel {
    public class Position {
        public int X;
        public int Y;
    }
    
    Task[][] inputs;
    public ConcatVisionChannel(Nar nar, SensoryChannel reportResultsTo, int width, int height) {
        super(nar,reportResultsTo, width, height, -1, new Term("BRIGHT"));
        inputs = new Task[height][width];
    }
    
    public void AddToSpatialBag(Task t) {
        int x = t.getTerm().term_indices[2];
        int y = t.getTerm().term_indices[3];
        t.incPriority((float) this.topDownPriority(t.getTerm()));
        inputs[y][x] = t;
        Position pos = new Position();
        pos.X = x;
        pos.Y = y;
        sampling.add(pos); //another vote for this position
    }
    
    List<Position> sampling = new ArrayList<>(); //TODO replace duplicates by using counter
    @Override
    public Nar addInput(final Task t, final Timable time) {
        AddToSpatialBag(t);
        step_start(time); //just input driven for now   
        return nar; //but could as well listen to nar cycle end or even spawn own thread instead
    }
    
    @Override
    public void step_start(final Timable time)
    {
        Position samplePos = sampling.get(0);
        Task current = inputs[samplePos.Y][samplePos.X];
        int k=0;
        for(int i=1;i<sampling.size();i++) {
            Position samplePos2 = sampling.get(i);
            Task prem2 = inputs[samplePos2.Y][samplePos2.X];
            List<Task> seq = Buffer.proceedWithTemporalInduction(current.sentence, prem2.sentence, prem2, 
                                                              new DerivationContext(nar.memory, nar.narParameters, time), true, false, true, false);
            if(seq != null) {
                for(Task t : seq) {
                    if(!t.sentence.isEternal()) { //TODO improve API, this check should not be necessary
                        current = t;
                        break;
                    }
                }
            }
            k++;
        }
        System.out.println(k);
        System.out.println(current);
        this.results.add(current);//feeds results into "upper" sensory channels:
        this.step_finished(time); 
    }
    
}
