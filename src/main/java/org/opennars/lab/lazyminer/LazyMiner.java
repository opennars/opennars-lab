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

//--------------------------LazyMiner----------------------------------

import org.opennars.entity.Sentence;
import org.opennars.entity.Task;
import org.opennars.io.events.EventEmitter;
import org.opennars.io.events.Events;
import org.opennars.main.Nar;
import org.opennars.operator.NullOperator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

// For mining relationships between events under the 
// Assumption of Insufficient Knowledge and Resources.
// It extends the OpenNARS class and implements the
// EventObserver interface for handling reasoner-internal events.
//---------------------------------------------------------------------

public class LazyMiner extends Nar implements EventEmitter.EventObserver {
	
    //""""""""""""""""""""""""""""""""""""""""""
    //""NARS configuration and event listeners""
    //""""""""""""""""""""""""""""""""""""""""""
    
    //Constructor with LazyMiner specific configuration:
    public LazyMiner() throws IOException, ClassNotFoundException, 
            IllegalAccessException, ParseException, ParserConfigurationException, SAXException, 
            NoSuchMethodException, InstantiationException, InvocationTargetException {
        //Set listening to query answer events of itself to true:
        //These are reasoner-internal events for plugins etc., not NAL7 events.
        this.memory.event.set(this, true, Events.Answer.class); 
    }

    //Handle reasoner-internal events:
    @Override
    public void event(Class event, Object[] args) {
        //Query answered:
        if (event == Events.Answer.class) {
            //The query task:
            Task query = (Task) args[0];
            //and the solution that was found:
            Sentence newBestSolution = query.getBestSolution();
            //Output the solution
            System.out.println("Answer: " + newBestSolution.toString());
        }
    }
    
    //""""""""""""""""""""""""""""""""""""""""""
    //""""""""""""""LazyMiner API"""""""""""""""
    //""""""""""""""""""""""""""""""""""""""""""

    //Discretization for numeric values 
    public int discretize(int value, int accurracy) { //value 114 with accurracy 10 gives 110
        return (value + accurracy/2) / accurracy * accurracy;
    }
    
    //The used discretization accuracy for values with getter and setter:
    public int discretization = 10;
    public void setDiscretization(int val)
    {
        this.discretization = val;
    }
    public int getDiscretization(int val)
    {
        return discretization;
    }
    

    //Adding an attribute numeric value event for an instance:
    public void AddAttributeEvent(String instance, int value, String attribute) {
        this.addInput("<(*,{" + instance + "}," + String.valueOf(discretize(value,discretization)) + ") --> " + attribute + ">. :|:");
    }
    
    //Adding an attribute value event for an instance:
    public void AddAttributeEvent(String instance, String value, String attribute) {
        this.addInput("<(*,{" + instance + "}," + value + ") --> " + attribute + ">. :|:");
    }
    
    //Add a property event for an instance:
    public void AddPropertyEvent(String instance, String property) {
        this.addInput("<{" + instance + "} --> [" + name + "]>. :|:");
    }

    //Add a sensory value that belong to a specific sensor, for instance name being "heartrate" and value being 50:
    public void AddSensorEvent(String name, int value) {
        this.addInput("<{"+String.valueOf(discretize(value,discretization))+"} --> " + name + ">. :|:");
    }
    
    //The same for an array of values, currently using a product encoding:
    public void AddSensorEvent(String name, int[] value) {
        String s = "";
        for(int i=0;i<value.length;i++) {
            s += String.valueOf(discretize(value[i],discretization)) + ",";
        }
        s=s.substring(0, s.length()-1);
        this.addInput("<{(*,"+String.valueOf(s)+")} --> " + name + ">. :|:");
    }

    //Add an activity, that is mapped as an operation:
    public void AddActivity(String name) {
        if(this.memory.getOperator("^"+name) == null) {
            this.memory.addOperator(new NullOperator("^" + name));
        }
        this.addInput("<(*,{SELF}) --> ^" + name + ">. :|:");
    }

    //Query how a sensor reached a certain value:
    public void HowSensorEventReachedValue(String name, int value) {
        String event = "<{" + String.valueOf(discretize(value,discretization)) + "} --> " + name + ">";
        String question = "<(&/,?op,?i,?how,?i2) =/> " + event + ">?";
        this.addInput(question);
    }
    
    //Query how a sensor reached a certain array of values:
    public void HowSensorEventReachedValue(String name, int[] value) {
        String s = "";
        for(int i=0;i<value.length;i++) {
            s += String.valueOf(discretize(value[i],discretization)) + ",";
        }
        s=s.substring(0, s.length()-1);
        String event = "<{(*,"+String.valueOf(s)+")} --> "+ name +">";
        String question = "<(&/,<(*,{SELF}) --> ?op>,?i,?how,?i2) =/> " + event + ">?";
        this.addInput(question);
    }
    
    //Adding an attribute numeric value event for an instance:
    public void HowAttributeReachedValue(String instance, int value, String attribute) {
        String event = "<(*,{" + instance + "}," + String.valueOf(discretize(value,discretization)) + ") --> " + attribute + ">";
        String question = "<(&/,<(*,{SELF}) --> ?op>,?i,?how) =/> " + event + ">?";
        this.addInput(question);
    }
    
    //Adding an attribute value event for an instance:
    public void HowAttributeReachedValue(String instance, String value, String attribute) {
        String event = "<(*,{" + instance + "}," + value + ") --> " + attribute + ">";
        String question = "<(&/,<(*,{SELF}) --> ?op>,?i,?how) =/> " + event + ">?";
        this.addInput(question);
    }
    
    //Add a property event for an instance:
    public void HowPropertyWasFulfilled(String instance, String property) {
        String event = "<{" + instance + "} --> ["+ name +"]>";
        String question = "<(&/,<(*,{SELF}) --> ?op>,?i,?how) =/> " + event + ">?";
        this.addInput(question);
    }
}
