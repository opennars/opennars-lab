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

package org.opennars.lab.nn.slim;

import java.util.ArrayList;
import java.util.List;

/**
 * Self-delimiting neural network as described by Schmidhuber
 *
 * The network can decide itself when the computations are done
 */
public class SelfDelimitingNeuralNetwork {
    public List<Neuron> neurons = new ArrayList<>();

    public List<Neuron> new_ = new ArrayList<>();
    public List<Neuron> old_ = new ArrayList<>();

    public List<Neuron> trace = new ArrayList<>();

    public double[] inputVector;

    public int haltNeuronIdx = -1;

    public boolean debug = false;

    public void run() {
        for(int i=0;i<10;i++) {
            Neuron haltNeuron = neurons.get(haltNeuronIdx);

            // TODO< seems to be buggy because it is not triggered
            // // we can't use isFiring because it was normalized!
            if(haltNeuron.now != 0.0 ) {
                break;
            }

            propagate();
        }
    }

    protected void propagate() {
        if(this.debug) {
            System.out.println("propagate");
        }

        double[] inputVector = this.inputVector;

        // transfer input vector
        for(int i=0;i<inputVector.length;i++) {
            //console.log("input[" + i + "]=" + inputVector[i]);

            neurons.get(i).now = inputVector[i];
        }

        for(int i=0;i<inputVector.length;i++) {
            if(neurons.get(i).now != 0.0) {
                old_.add(neurons.get(i));
            }
        }



        // activation spreading
        for(int idxOld=0; idxOld<old_.size(); idxOld++) {
            Neuron iOld = old_.get(idxOld);

            for(int iOutSynapseIdx=0;iOutSynapseIdx<iOld.outputNeuronIndicesWithWeights.size();iOutSynapseIdx++) {
                final Neuron.OutputNeuronIndexWithWeight iOutSynapse = iOld.outputNeuronIndicesWithWeights.get(iOutSynapseIdx);

                if(iOutSynapse.weight == 0.0) {
                    continue;
                }

                // marking
                if(!iOutSynapse.mark) {
                    // append c^lk to trace
                    // TODO< >
                }
                iOutSynapse.mark = true;


                Neuron outNeuron = this.neurons.get(iOutSynapse.idx);

                if(outNeuron.type.equals("add")) {
                    outNeuron.next += (iOutSynapse.weight*iOld.now);
                }
                else if(outNeuron.type.equals("mul")) {
                    outNeuron.next *= (iOutSynapse.weight*iOld.now);
                }

                if(!outNeuron.used) {
                    new_.add(outNeuron); // append
                }
                outNeuron.used = true;

                // TODO< limit T >
            }
        }

        for(int i=0; i<new_.size(); i++) {
            // TODO< WTA >
            new_.get(i).now = new_.get(i).next;
            new_.get(i).now = new_.get(i).isFiring() ? 1.0 : 0.0;

            if(this.debug) {
                //console.log("b " + this.new_[i].now);
                //console.log("idx " + this.new_[i].idx);
            }

            new_.get(i).used = false;
        }

        for(int i=0;i<new_.size();i++) {
            if(new_.get(i).type.equals("add")) {
                new_.get(i).next = 0.0;
            }
            else {
                new_.get(i).next = 1.0;
            }
        }


        old_ = new_;
        new_ = new ArrayList<>();

        // delete from old all with zero now
        for(int i=0;i<this.old_.size();i++) {
            if( old_.get(i).now == 0.0 ) {
                old_.remove(i);
                i--;
            }
        }

        // execute environment chaning actions
        // TODO
    }
}
