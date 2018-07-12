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
package org.opennars.lab.nalnet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.xml.parsers.ParserConfigurationException;
import org.opennars.main.Nar;
import org.opennars.main.MiscFlags;
import org.opennars.entity.BudgetValue;
import org.opennars.entity.Sentence;
import org.opennars.entity.Stamp;
import org.opennars.entity.Stamp.BaseEntry;
import org.opennars.entity.Task;
import org.opennars.entity.TruthValue;
import org.opennars.inference.BudgetFunctions;
import org.opennars.inference.TemporalRules;
import org.opennars.inference.TruthFunctions;
import org.opennars.io.Symbols;
import org.opennars.language.Conjunction;
import org.opennars.language.Negation;
import org.opennars.language.Term;
import org.opennars.main.Parameters;
import org.xml.sax.SAXException;

/**
 *
 * @author Patrick
 */

public class NALNetwork
{
    
    public class NALNode
    {
        public Term term;
        public TruthValue truth;
        public BaseEntry[] evidentalBase;
        public NALNode[] neighbours; //the "neighbours" the truth calculation is based on
        public boolean negated;
        Parameters narParameters;
        
        //A NALNode can be a term with a certain truth
        public NALNode(Parameters narParameters, Term term, TruthValue truth, long base) {
            this(narParameters, term, truth, base, false);
        }
        public NALNode(Parameters narParameters, Term term, TruthValue truth, long base, boolean negated) {
            this.negated = negated;
            if(negated) {
                this.term = Negation.make(term);
                this.truth = TruthFunctions.negation(truth, narParameters);
            } else {
                this.term = term;
                this.truth = truth;
            }
            this.evidentalBase = new BaseEntry[] { new BaseEntry(0, base) };
        }
        //or is a composition of neighbours
        public NALNode(NALNode[] neighbours) {
            this(neighbours, false);
        }
        public NALNode(NALNode[] neighbours, boolean negated) {
            this.neighbours = neighbours;
            this.negated = negated;
        }
        
        boolean calculated = false;
        public TruthValue calculate() {
            calculated = true;
            if(truth != null) {
                return truth;
            }
            List<Term> components = new LinkedList<Term>();
            
            HashSet<BaseEntry> evidence_bases = new HashSet<>();
            for (NALNode neighbour : neighbours) {
                if (neighbour.calculate() == null) {
                    continue;
                }
                TruthValue t = neighbour.truth;
                Term component = neighbour.term;
                if (truth == null) {
                    truth = t;
                    for(BaseEntry ent : neighbour.evidentalBase) {
                        evidence_bases.add(ent);
                    }
                    components.add(component);
                } else {
                    BaseEntry[] arr = new BaseEntry[evidence_bases.size()];
                    int k=0;
                    for(BaseEntry ent : evidence_bases) {
                        arr[k++] = ent;
                    }
                    if (!Stamp.baseOverlap(arr, neighbour.evidentalBase)) {
                        truth = TruthFunctions.intersection(truth, t, narParameters);
                        for(BaseEntry ent : neighbour.evidentalBase) {
                            evidence_bases.add(ent);
                        }
                        components.add(component);
                    }
                }
            }
            
            this.evidentalBase = new BaseEntry[evidence_bases.size()];
            int k = 0;
            for(BaseEntry ent : evidence_bases) {
                this.evidentalBase[k++] = ent;
            }
            if(this.neighbours.length > 0) {
                try {
                    this.term = Conjunction.make(components.toArray(new Term[0]), TemporalRules.ORDER_CONCURRENT);
                }catch(Exception ex){}
            }
            if(this.negated) {
                this.term = Negation.make(this.term);
                this.truth = TruthFunctions.negation(this.truth, narParameters);
            }
            return this.truth;
        }
        
        @Override
        public String toString() {
            if(this.truth == null || this.term == null) {
                if(calculated) {
                    return "No connection to input evidence";
                } else {
                    return "Node not evaluated";
                }
            }
            String evidences = "";
            for(BaseEntry s : this.evidentalBase) {
                evidences += s + ",";
            }
            if(!evidences.isEmpty()) {
                evidences = evidences.substring(0, evidences.length()-1);
            }
            return this.term.toString() + ". :|: " + this.truth.toString() + " {" + evidences + "}";
        }
        
        public void inputInto(Nar nar) {
            calculate();
            Stamp stamp = new Stamp(nar,nar.memory);
            stamp.setOccurrenceTime(nar.time());
            Sentence sentence = new Sentence(this.term, 
                                             Symbols.JUDGMENT_MARK, 
                                             this.truth, 
                                             stamp);
            Task task = new Task(sentence, 
                                 new BudgetValue(narParameters.DEFAULT_JUDGMENT_PRIORITY,
                                                 narParameters.DEFAULT_JUDGMENT_DURABILITY,
                                                 BudgetFunctions.truthToQuality(this.truth), narParameters),
                                 Task.EnumType.INPUT);
            nar.addInput(task, nar);
        }
    }
    
    Random rnd = new Random();
    public class NALNet 
    {    
        public boolean[][] negated;
        Parameters narParameters;
        public NALNet(Parameters narParameters, boolean[][] negated) { //how many layers and what amount of nodes per layer?
            this.negated = negated;
            this.narParameters = narParameters;
        }
        
        public float[] input(float[] input_frequencies) { //default confidence
            TruthValue[] input_values = new TruthValue[input_frequencies.length];
            for(int i=0; i<input_frequencies.length; i++) {
                input_values[i] = new TruthValue(input_frequencies[i], narParameters.DEFAULT_JUDGMENT_CONFIDENCE, narParameters);
            }
            TruthValue[] output_values = input(input_values);
            float[] output_frequencies = new float[output_values.length];
            for(int i=0; i<output_values.length; i++) {
                output_frequencies[i] = output_values[i].getFrequency();
            }
            return output_frequencies;
        }

        NALNode[] outputs;
        TruthValue[] output_truths;
        Term[] output_terms;
        public TruthValue[] input(TruthValue[] input_values) {
            if(output_truths != null) {
                return output_truths;
            }
            NALNode[][] network = new NALNode[this.negated.length][];
            NALNode[] inputs = new NALNode[this.negated[0].length];
            for(int i=0; i<inputs.length; i++) {
                inputs[i] = new NALNode(narParameters, new Term("input" + (i+1)),input_values[i], i+1, this.negated[0][i]);
            }
            network[0] = inputs;
            //ok create the succeeding layers too
            for(int layer = 1; layer < this.negated.length; layer++) {
                network[layer] = new NALNode[this.negated[layer].length];
                //whereby each node in next layer is connected to a subset of previous
                for(int i=0; i<network[layer].length; i++) {
                    List<NALNode> toAdd = new LinkedList<NALNode>();
                    for(NALNode previous : network[layer-1]) {
                        toAdd.add(previous);
                    }
                    network[layer][i] = new NALNode(toAdd.toArray(new NALNode[0]), this.negated[layer][i]);
                }
            }
            //ok calculate the outputs now:
            outputs = network[network.length-1];
            output_truths = new TruthValue[outputs.length];
            output_terms = new Term[outputs.length];
            for(int i=0; i<outputs.length; i++) {
                NALNode output_node = outputs[i];
                outputs[i] = output_node;
                output_truths[i] = output_node.calculate();
                if(output_truths[i] == null) { //it wasn't connected to anything
                    output_truths[i] = new TruthValue(0.0f,0.0f, narParameters);
                }
                output_terms[i] = output_node.term;
            }
            return output_truths;
        }
    }
    
    public void demoNALNode(Parameters narParameters) {
        NALNode node1 = new NALNode(narParameters, new Term("input1"),new TruthValue(1.0f,0.9f, narParameters),1);
        NALNode node2 = new NALNode(narParameters, new Term("input2"),new TruthValue(0.4f,0.9f, narParameters),2,true);
        NALNode node3 = new NALNode(narParameters, new Term("input3"),new TruthValue(0.6f,0.9f, narParameters),3);
        NALNode node4 = new NALNode(narParameters, new Term("input4"),new TruthValue(0.6f,0.9f, narParameters),4);
        NALNode result = new NALNode(new NALNode[]{node1, node2, node3, node4}, false);
        result.calculate();
        String res = result.toString();
        assert(res.equals("(&|,(--,input2),input1,input3,input4). :|: %0.22;0.66% {1,2,3,4}"));
        System.out.println(res);
    }
    
    //similar as before but with working with layers
    public void demoNALNet(Parameters narParameters) {
        rnd.setSeed(1);
        NALNet nalnet = new NALNet(narParameters, new boolean[][]{new boolean[]{false,true,false,false},new boolean[]{false}});
        float[] result = nalnet.input(new float[]{1.0f, 0.4f, 0.6f, 0.6f});
        for(int i=0;i<result.length;i++) {
            System.out.println(nalnet.outputs[i]);
        }
    }

    public static void main(String args[]) throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, 
            ParserConfigurationException, IllegalAccessException, SAXException, ClassNotFoundException, ParseException {
        Parameters narParameters = new Nar().narParameters;
        NALNetwork nalnet = new NALNetwork();
        nalnet.demoNALNode(narParameters);
        nalnet.demoNALNet(narParameters);
    }
}
