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
package org.opennars.lab.plugin.input;

import java.util.ArrayList;
import org.opennars.io.events.EventEmitter;
import org.opennars.io.events.Events;
import org.opennars.main.Nar;
import org.opennars.main.MiscFlags;
import org.opennars.plugin.Plugin;
import org.opennars.control.DerivationContext;
import org.opennars.entity.BudgetValue;
import org.opennars.entity.Concept;
import org.opennars.entity.Sentence;
import org.opennars.entity.Stamp;
import org.opennars.entity.Stamp.BaseEntry;
import org.opennars.entity.Task;
import org.opennars.entity.TruthValue;
import org.opennars.inference.BudgetFunctions;
import static org.opennars.inference.TemporalRules.ORDER_CONCURRENT;
import static org.opennars.inference.TemporalRules.ORDER_FORWARD;
import org.opennars.inference.TruthFunctions;
import org.opennars.io.Symbols;
import org.opennars.language.Conjunction;
import org.opennars.language.Interval;
import org.opennars.language.Term;

/**
 *
 * @author tc
 */
public class PerceptionAccel implements Plugin, EventEmitter.EventObserver {

    private Nar n;
    @Override
    public boolean setEnabled(Nar n, boolean enabled) {
        //register listening to new events:
        this.n = n;
        n.memory.event.set(this, enabled, Events.InduceSucceedingEvent.class, Events.ConceptNew.class, Events.ConceptForget.class);
        return true;
    }
    
    double partConceptsPrioThreshold=0.1;
    public void setPartConceptsPrioThreshold(double value) {
        partConceptsPrioThreshold=value;
    }
    
    public double getPartConceptsPrioThreshold() {
        return partConceptsPrioThreshold;
    }
    
    ArrayList<Task> eventbuffer=new ArrayList<>();
    int cur_maxlen=1;
    
    public void perceive(DerivationContext nal) { //implement Peis idea here now
        //we start with length 2 compounds, and search for patterns which are one longer than the longest observed one
        
        boolean longest_result_derived_already=false;
        for(int Len=cur_maxlen+1;Len>=2;Len--) {
            //ok, this is the length we have to collect, measured from the end of event buffer
            Term[] relterms=new Term[2*Len-1]; //there is a interval term for every event
            //measuring its distance to the next event, but for the last event this is obsolete
            //thus it are 2*Len-1] terms

            Task newEvent=eventbuffer.get(eventbuffer.size()-1);
            TruthValue truth=newEvent.sentence.truth;
            Stamp st=new Stamp(nal.memory);
            ArrayList<BaseEntry> evBase=new ArrayList<>();
            
            int k=0;
            for(int i=0;i<Len;i++) {
                int j=eventbuffer.size()-1-(Len-1)+i; //we go till to the end of the event buffer
                if(j<0) { //event buffer is not filled up enough to support this one, happens at the beginning where event buffer has no elements
                     //but the mechanism already looks for length 2 patterns on the occurence of the first event
                    break;
                }
                Task current=eventbuffer.get(j);
                for(BaseEntry l : current.sentence.stamp.evidentialBase) {
                    evBase.add(l);
                }
                
                relterms[k]=current.sentence.term;
                if(i!=Len-1) { //if its not the last one, then there is a next one for which we have to put an interval
                    truth=TruthFunctions.deduction(truth, current.sentence.truth, n.narParameters);
                    Task next=eventbuffer.get(j+1);
                    relterms[k+1]=new Interval(next.sentence.getOccurenceTime()-current.sentence.getOccurenceTime());
                }
                k+=2;
            }

            BaseEntry[] evB=new BaseEntry[evBase.size()];
            int u=0;
            for(BaseEntry l : evBase) {
                evB[u]=l;
                u++;
            }
            st.baseLength=evB.length;
            st.evidentialBase=evB;
            
            boolean eventBufferDidNotHaveSoMuchEvents=false;
            for (Term relterm : relterms) {
                if (relterm == null) {
                    eventBufferDidNotHaveSoMuchEvents = true;
                }
            }
            if(eventBufferDidNotHaveSoMuchEvents) {
                continue;
            }
            //decide on the tense of &/ by looking if the first event happens parallel with the last one
            //Todo refine in 1.6.3 if we want to allow input of difference occurence time
            boolean after=newEvent.sentence.stamp.after(eventbuffer.get(eventbuffer.size()-1-(Len-1)).sentence.stamp, n.narParameters.DURATION);
            
            //critical part: (not checked for correctness yet):
            //we now have to look at if the first half + the second half already exists as concept, before we add it
            Term[] firstHalf;
            Term[] secondHalf;
            if(relterms[Len-1] instanceof Interval) {
                //the middle can be a interval, for example in case of a,+1,b , in which case we dont use it
                firstHalf=new Term[Len-1]; //so we skip the middle here
                secondHalf=new Term[Len-1]; //as well as here
                int h=0; //make index mapping easier by counting
                for(int i=0;i<Len-1;i++) {
                    firstHalf[i]=relterms[h];
                    h++;
                }
                h+=1; //we have to overjump the middle element this is why
                for(int i=0;i<Len-1;i++) {
                    secondHalf[i]=relterms[h];
                    h++;
                }
            } else { //it is a event so its fine
                firstHalf=new Term[Len]; //2*Len-1 in total
                secondHalf=new Term[Len]; //but the middle is also used in the second one
                int h=0; //make index mapping easier by counting
                for(int i=0;i<Len;i++) {
                    firstHalf[i]=relterms[h];
                    h++;
                }
                h--; //we have to use the middle twice this is why
                for(int i=0;i<Len;i++) {
                    secondHalf[i]=relterms[h];
                    h++;
                }
            }
            Term firstC=Conjunction.make(firstHalf, after ? ORDER_FORWARD : ORDER_CONCURRENT);
            Term secondC=Conjunction.make(secondHalf, after ? ORDER_FORWARD : ORDER_CONCURRENT);
            Concept C1=nal.memory.concept(firstC);
            Concept C2=nal.memory.concept(secondC);
            
            if(C1==null || C2==null) {
                if(debugMechanism) {
                    System.out.println("one didn't exist: "+firstC.toString()+" or "+secondC.toString());
                }
                continue; //the components were not observed, so don't allow creating this compound
            }
            
            if(C1.getPriority()<partConceptsPrioThreshold || C2.getPriority()<partConceptsPrioThreshold) {
                continue; //too less priority
            }
            
            Conjunction C=(Conjunction) Conjunction.make(relterms, after ? ORDER_FORWARD : ORDER_CONCURRENT);
            
            Sentence S=new Sentence(C,Symbols.JUDGMENT_MARK,truth,st); //importance "summation"
            Task T=new Task(S,new BudgetValue(BudgetFunctions.or(C1.getPriority(), C2.getPriority()),n.narParameters.DEFAULT_JUDGMENT_DURABILITY,truth,n.narParameters), Task.EnumType.INPUT);
            
            if(debugMechanism) {
                System.out.println("success: "+T.toString());
            }
            
            if(longest_result_derived_already) {
                T.setElemOfSequenceBuffer(false);
            }
            
            longest_result_derived_already=true;
            
            nal.derivedTask(T, false, false, false); //lets make the new event the parent task, and derive it
        }
    }
    
    //keep track of how many conjunctions with related amount of component terms there are:
    int sz=100;
    int[] sv=new int[sz]; //use static array, should suffice for now
    boolean debugMechanism=false;
    public void handleConjunctionSequence(Term t, boolean Add) {
        if(!(t instanceof Conjunction)) {
            return;
        }
        Conjunction c=(Conjunction) t;
        
        if(debugMechanism) {
            System.out.println("handleConjunctionSequence with "+t.toString()+" "+String.valueOf(Add));
        }
        
        if(Add) { //manage concept counter
            sv[c.term.length]++; 
        } else {
            sv[c.term.length]--;
        }
        //determine cur_maxlen 
        //by finding the first complexity which exists
        cur_maxlen=1; //minimum size is 1 (the events itself), in which case only chaining of two will happen
        for(int i=sz-1;i>=2;i--) { //>=2 because a conjunction with size=1 doesnt exist
            if(sv[i]>0) {
                cur_maxlen=i; //dont using the index 0 in sv makes it easier here
                break;
            }
        }
        
        if(debugMechanism) {
            System.out.println("determined max len is "+String.valueOf(cur_maxlen));
        }
    }
    
    @Override
    public void event(Class event, Object[] args) {
        if (event == Events.InduceSucceedingEvent.class) { //todo misleading event name, it is for a new incoming event
            Task newEvent = (Task)args[0];
            if(newEvent.sentence.punctuation==Symbols.JUDGMENT_MARK) {
                eventbuffer.add(newEvent);
                while(eventbuffer.size()>cur_maxlen+1) {
                    eventbuffer.remove(0);
                }
                DerivationContext nal= (DerivationContext)args[1];
                perceive(nal);
            }
        }
        if(event == Events.ConceptForget.class) {
            Concept forgot=(Concept) args[0];
            handleConjunctionSequence(forgot.term,false);
        }
        if(event == Events.ConceptNew.class) {
            Concept newC=(Concept) args[0];
            handleConjunctionSequence(newC.term,true);
        }
    } 
    
    public static int PERCEPTION_DECISION_ACCEL_SAMPLES = 1; //new inference rule accelerating decision making: https://groups.google.com/forum/#!topic/open-nars/B8veE-WDd8Q
    //mostly only makes sense if perception plugin is loaded
}
