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

package org.opennars.lab.nn;

import org.opennars.lab.common.math.DualNumber;

// TODO bias
/**
 * Classical feed forward neural network
 */
public class FeedForwardNeuralNetwork {
    // each element are just the weights for all inputs
    public DualNumber[][] hiddenNeurons;

    // each element are the weight for all hidden neurons
    public DualNumber[][] outputNeurons;

    public float[] input;

    public DualNumber[] outputActivations;

    public void feedforward() {
        DualNumber[] hiddenNeuronActivations = new DualNumber[this.hiddenNeurons.length];
        // hidden neurons
        for(int iHiddenNeuronIdx=0;iHiddenNeuronIdx<this.hiddenNeurons.length;iHiddenNeuronIdx++) {
            final DualNumber[] iHiddenNeuron = this.hiddenNeurons[iHiddenNeuronIdx];

            DualNumber res = new DualNumber();
            res.diff = new double[retDiffWidth()];

            for(int i=0;i<this.input.length;i++) {
                DualNumber inputDualNumber = new DualNumber(this.input[i]);
                inputDualNumber.diff = new double[retDiffWidth()];

                final DualNumber mulRes = DualNumber.mul(iHiddenNeuron[i], inputDualNumber);
                res = DualNumber.additiveRing(res, mulRes, 1);
            }
            hiddenNeuronActivations[iHiddenNeuronIdx] = res;
        }

        // output neurons
        for(int iOutputNeuronIdx=0;iOutputNeuronIdx<this.outputNeurons.length;iOutputNeuronIdx++) {
            DualNumber[] iOutputNeuron = this.outputNeurons[iOutputNeuronIdx];

            DualNumber res = new DualNumber();
            res.diff = new double[retDiffWidth()];

            for(int i=0;i<this.hiddenNeurons.length;i++) {
                final DualNumber mulRes = DualNumber.mul(iOutputNeuron[i], hiddenNeuronActivations[i]);
                res = DualNumber.additiveRing(res, mulRes, 1);
            }
            this.outputActivations[iOutputNeuronIdx] = res;
        }
    }

    /**
     * @return size of the automatic differential array
     */
    protected int retDiffWidth() {
        // we can do this because we assume that every element has the same size of the diff array
        return hiddenNeurons[0][0].diff.length;
    }
}
