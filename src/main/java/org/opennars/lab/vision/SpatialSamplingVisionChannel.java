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
import static org.opennars.control.TemporalInferenceControl.proceedWithTemporalInduction;
import org.opennars.entity.Sentence;
import org.opennars.entity.Task;
import org.opennars.plugin.perception.SensoryChannel;
import org.opennars.language.Term;
import org.opennars.storage.LevelBag;
import org.opennars.storage.Memory;

public class SpatialSamplingVisionChannel extends SensoryChannel {
    public class Position {
        public int X;
        public int Y;
    }
    
    LevelBag<Task<Term>,Sentence<Term>>[][] spatialbag;
    public SpatialSamplingVisionChannel(Nar nar, SensoryChannel reportResultsTo, int width, int height) {
        super(nar,reportResultsTo, width, height, width*height, new Term("BRIGHT"));
        spatialbag = new LevelBag[height][width];
    }
    
    public void AddToSpatialBag(Task t) {
        int x = t.getTerm().term_indices[2];
        int y = t.getTerm().term_indices[3];
        if(spatialbag[y][x] == null) {
            spatialbag[y][x] = new LevelBag(100, 100);
        }
        t.incPriority((float) this.topDownPriority(t.getTerm()));
        spatialbag[y][x].putIn(t);
        Position pos = new Position();
        pos.X = x;
        pos.Y = y;
        sampling.add(pos); //another vote for this position
    }
    
    List<Position> sampling = new ArrayList<>(); //TODO replace duplicates by using counter
    @Override
    public Nar addInput(Task t) {
        int[] test = t.getTerm().term_indices;
        AddToSpatialBag(t);
        for(int i=0;i<100000;i++) {
            step_start(); //just input driven for now   
        }
        return nar; //but could as well listen to nar cycle end or even spawn own thread instead
    }
    
    @Override
    public void step_start()
    {
        int ind = Memory.randomNumber.nextInt(sampling.size());
        Position samplePos = sampling.get(ind);
        Task sampled = spatialbag[samplePos.Y][samplePos.X].takeNext();
        //Todo improve API, channel should not need to know where in the array x and y size is
        
        //spatial biased random sampling: 
        int ind2 = Memory.randomNumber.nextInt(sampling.size());
        int s2posY = sampling.get(ind2).Y;
        int s2posX = sampling.get(ind2).X;
        if(spatialbag[s2posY][s2posX] != null) {
            Task sampled2 = spatialbag[s2posY][s2posX].takeNext();
            if(sampled2 != null) {
                //improve API, carrying out temporal inference should be easier..
                List<Task> seq = proceedWithTemporalInduction(sampled.sentence, sampled2.sentence, sampled2, 
                                                              new DerivationContext(nar.memory, nar.narParameters), true, false, true);
                if(seq != null) {
                    for(Task t : seq) {
                        if(!t.sentence.isEternal()) { //TODO improve API, this check should not be necessary
                            AddToSpatialBag(t);
                            this.results.add(t);
                        }
                    }
                }
                //todo improve API, putting an element bag should be easy
                spatialbag[s2posY][s2posX].putBack(sampled2, nar.memory.cycles(nar.memory.param.conceptForgetDurations), nar.memory);
            }
        }
        spatialbag[samplePos.Y][samplePos.X].putBack(sampled, nar.memory.cycles(nar.memory.param.conceptForgetDurations), nar.memory);
        //feeds results into "upper" sensory channels:
        this.step_finished(); 
    }
    
}
