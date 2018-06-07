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

package org.opennars.lab.plugin.mental;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.opennars.io.events.EventEmitter;
import org.opennars.io.events.Events;
import org.opennars.main.Nar;
import org.opennars.main.Parameters;
import org.opennars.plugin.Plugin;
import org.opennars.control.DerivationContext;
import org.opennars.entity.BudgetValue;
import org.opennars.entity.Concept;
import org.opennars.entity.Sentence;
import org.opennars.entity.Stamp;
import org.opennars.entity.Task;
import org.opennars.entity.TruthValue;
import org.opennars.inference.TemporalRules;
import org.opennars.io.Symbols;
import org.opennars.language.Conjunction;
import org.opennars.language.Implication;
import org.opennars.language.Interval;
import org.opennars.language.Term;
import org.opennars.language.Variables;

/**
 *
 * @author tc
 */
public class GlobalAnticipation implements Plugin, EventEmitter.EventObserver {

    public final ArrayDeque<Task> stm = new ArrayDeque();
    public final List<Task> current_tasks=new ArrayList<Task>();
    int MatchUpTo=20;
    
    public void setMatchEventsMax(double value) {
        MatchUpTo=(int) value;
    }
    
    public double getMatchEventsMax() {
        return MatchUpTo;
    }
    
    public double TEMPORAL_PREDICTION_FEEDBACK_ACCURACY_DIV=0.01;
    
    public double getTemporalAccuracy() {
        return TEMPORAL_PREDICTION_FEEDBACK_ACCURACY_DIV;
    }
    
    public void setTemporalAccuracy(double value) {
        TEMPORAL_PREDICTION_FEEDBACK_ACCURACY_DIV=value;
    }
    
    @Override
    public void event(Class event, Object[] args) {
        if (event == Events.TaskDerive.class) {
            Task derivedTask=(Task) args[0];
            if(derivedTask.sentence.term instanceof Implication &&
               (derivedTask.sentence.term.getTemporalOrder()==TemporalRules.ORDER_FORWARD ||
                    derivedTask.sentence.term.getTemporalOrder()==TemporalRules.ORDER_CONCURRENT)) {

                if(!current_tasks.contains(derivedTask)) {
                    current_tasks.add(derivedTask);
                }
            }
        }
        else if (event == Events.ConceptBeliefRemove.class) {
            Task removedTask=(Task) args[2]; //task is 3nd
            current_tasks.remove(removedTask);
        }
        else if (event == Events.InduceSucceedingEvent.class) {            
            Task newEvent = (Task)args[0];
            DerivationContext nal= (DerivationContext)args[1];
            
            if (newEvent.sentence.truth!=null) {
                stm.add(newEvent);
                while(stm.size()>MatchUpTo) {
                    stm.removeFirst();
                }
            }
            
            temporalPredictionsAdapt(nal);
        }
    }  
    
    //check all predictive statements, match them with last events
    public void temporalPredictionsAdapt(DerivationContext nal) {
        if(TEMPORAL_PREDICTION_FEEDBACK_ACCURACY_DIV==0.0f) {
            return; 
        }
        
        ArrayList<Task> lastEvents=new ArrayList<Task>();
        for (Task stmLast : stm) {
            lastEvents.add(stmLast);
        }
        
        if(lastEvents.isEmpty()) {
            return;
        }
        
        final long duration = Parameters.DURATION;
        ArrayList<Task> derivetasks=new ArrayList<Task>();
        
        for(final Task c : current_tasks) { //a =/> b or (&/ a1...an) =/> b
            boolean concurrent_conjunction=false;
            Term[] args=new Term[1];
            Implication imp=(Implication) c.sentence.term.clone();
            boolean concurrent_implication=imp.getTemporalOrder()==TemporalRules.ORDER_CONCURRENT;
            args[0]=imp.getSubject();
            if(imp.getSubject() instanceof Conjunction) {
                Conjunction conj=(Conjunction) imp.getSubject();
                if(!conj.isSpatial) {
                    if(conj.temporalOrder==TemporalRules.ORDER_FORWARD || conj.temporalOrder==TemporalRules.ORDER_CONCURRENT) {
                        concurrent_conjunction=conj.temporalOrder==TemporalRules.ORDER_CONCURRENT;
                        args=conj.term; //in case of &/ this are the terms
                    }
                }
            }
            int i=0;
            boolean matched=true;
            int off=0;
            long expected_time=lastEvents.get(0).sentence.getOccurenceTime();
            
            for(i=0;i<args.length;i++) {
                //handling of intervals:
                if(args[i] instanceof Interval) {
                    if(!concurrent_conjunction) {
                        expected_time+=((Interval)args[i]).time;
                    }
                    off++;
                    continue;
                }

                if(i-off>=lastEvents.size()) {
                    break;
                }

                //handling of other events, seeing if they match and are right in time
                
                if(!Variables.hasSubstitute(Symbols.VAR_INDEPENDENT, args[i], lastEvents.get(i-off).sentence.term)) { //it didnt match, instead sth different unexpected happened
                    matched=false; //whether intermediate events should be tolerated or not was a important question when considering this,
                    break; //if it should be allowed, the sequential match does not matter only if the events come like predicted.
                } else { //however I decided that sequence matters also for now, because then the more accurate hypothesis wins.

                    if(lastEvents.get(i-off).sentence.truth.getExpectation()<=0.5f) { //it matched according to sequence, but is its expectation bigger than 0.5? todo: decide how truth values of the expected events
                        //it didn't happen
                        matched=false;
                        break;
                    }

                    long occurence=lastEvents.get(i-off).sentence.getOccurenceTime();
                    boolean right_in_time=Math.abs(occurence-expected_time) < ((double)duration)/TEMPORAL_PREDICTION_FEEDBACK_ACCURACY_DIV;
                    if(!right_in_time) { //it matched so far, but is the timing right or did it happen when not relevant anymore?
                        matched=false;
                        break;
                    }
                }

                if(!concurrent_conjunction) {
                    expected_time+=duration;
                }
            }

            if(concurrent_conjunction && !concurrent_implication) { //implication is not concurrent
                expected_time+=duration; //so here we have to add duration
            }
            else
            if(!concurrent_conjunction && concurrent_implication) {
                expected_time-=duration;
            } //else if both are concurrent, time has never been added so correct
              //else if both are not concurrent, time was always added so also correct

            //ok it matched, is the consequence also right?
            if(matched && lastEvents.size()>args.length-off) { 
                long occurence=lastEvents.get(args.length-off).sentence.getOccurenceTime();
                boolean right_in_time=Math.abs(occurence-expected_time)<((double)duration)/TEMPORAL_PREDICTION_FEEDBACK_ACCURACY_DIV;
                 
                if(right_in_time && Variables.hasSubstitute(Symbols.VAR_INDEPENDENT,imp.getPredicate(),lastEvents.get(args.length-off).sentence.term)) { //it matched and same consequence, so positive evidence
                    //c.sentence.truth=TruthFunctions.revision(c.sentence.truth, new TruthValue(1.0f,Parameters.DEFAULT_JUDGMENT_CONFIDENCE));
                    Sentence s2=new Sentence(
                        c.sentence.term.clone(),
                        Symbols.JUDGMENT_MARK,
                        new TruthValue(1.0f,Parameters.DEFAULT_JUDGMENT_CONFIDENCE),
                        new Stamp(nal.memory));

                    Task t=new Task(s2,new BudgetValue(Parameters.DEFAULT_JUDGMENT_PRIORITY,Parameters.DEFAULT_JUDGMENT_DURABILITY,s2.truth), Task.EnumType.INPUT);
                    derivetasks.add(t);
                } else { //it matched and other consequence, so negative evidence
                   // c.sentence.truth=TruthFunctions.revision(c.sentence.truth, new TruthValue(0.0f,Parameters.DEFAULT_JUDGMENT_CONFIDENCE));
                    Sentence s2=new Sentence(
                        c.sentence.term.clone(),
                        Symbols.JUDGMENT_MARK,
                        new TruthValue(0.0f,Parameters.DEFAULT_JUDGMENT_CONFIDENCE),
                        new Stamp(nal.memory));

                    Task t=new Task(s2,new BudgetValue(Parameters.DEFAULT_JUDGMENT_PRIORITY,Parameters.DEFAULT_JUDGMENT_DURABILITY,s2.truth), Task.EnumType.INPUT);
                    derivetasks.add(t);
                } //todo use derived task with revision instead
            }
        }
        for(Task t: derivetasks) {
            if(nal.derivedTask(t, false, false, false)) {
                boolean debug=true;
            }
        }
        ArrayList<Task> toDelete=new ArrayList<Task>();
        for(Task t: current_tasks) {
            Concept w=nal.memory.concept(t.sentence.term);
            if(w==null) { //concept does not exist anymore, delete
                toDelete.add(t);
            }
        }
        for(Task t: toDelete) {
            current_tasks.remove(t);
        }
    }
    
    @Override
    public boolean setEnabled(Nar n, boolean enabled) {
        //Events.TaskDerive.class Events.ConceptBeliefRemove.class
        n.memory.event.set(this, enabled, Events.InduceSucceedingEvent.class, Events.TaskDerive.class, Events.ConceptBeliefRemove.class);
        return true;
    }
    
}
