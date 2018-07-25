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
package org.opennars.util.test;

import org.opennars.main.Nar;
import org.opennars.entity.Concept;
import org.opennars.entity.Sentence;
import org.opennars.entity.Task;
import org.opennars.io.Narsese;
import org.opennars.io.Symbols;
import org.opennars.language.CompoundTerm;
import org.opennars.language.Term;

/**
 *
 * @author patrick.hammer
 */
public class ConceptMonitor {
    
    public static Term stringToTerm(Nar nar, String s) {
        Narsese narsese = new Narsese(nar.memory);
        Task ret;
        try {
            ret = narsese.parseTask(s + Symbols.JUDGMENT_MARK);
        } catch (Narsese.InvalidInputException ex) {
            return null;
        }
        if(ret == null) {
            return null;
        }
        return ret.getTerm();
    }
    
    public static Concept concept(Nar nar, String s) {
        Term ts = stringToTerm(nar, s);
        if(ts == null) {
            return null;
        }
        return nar.memory.concept(ts);
    }
    
    public static Sentence strongestProjectedInputEventBelief(Nar nar, String st) {
        Concept c = ConceptMonitor.concept(nar, st);
        if(c != null) {
            for(Task t : c.beliefs) {
                if(t.isInput() && !t.sentence.isEternal()) {
                    Sentence s = t.sentence;
                    Sentence projected = s.projection(nar.time(), nar.time(), nar.memory);
                    if(!projected.isEternal()) {
                        return projected;
                    }
                }
            }
        }
        return null;
    }
    
    public static Sentence strongestProjectedEternalizedBelief(Nar nar, String st) {
        Concept c = ConceptMonitor.concept(nar, st);
        if(c != null) {
            for(Task t : c.beliefs) {
                Sentence s = t.sentence;
                Sentence projected = s.projection(nar.time(), nar.time(), nar.memory);
                return projected;
            }
        }
        return null;
    }
    
    public static Sentence strongestPrecondition(Nar nar, String conc, String statement) {
        Concept c = ConceptMonitor.concept(nar, conc);
        Term st = stringToTerm(nar, statement);
        if(c != null && st != null) {
            for(Task t : c.executable_preconditions) {
                if(CompoundTerm.replaceIntervals(t.getTerm()).equals(
                        CompoundTerm.replaceIntervals(st))) {
                    return t.sentence;
                }
            }
        }
        return null;
    }
    
    public static Sentence strongestPrecondition2(Nar nar, String conc, String statement) { //test to compare with previous
        return strongestProjectedEternalizedBelief(nar, statement);
    }
}
