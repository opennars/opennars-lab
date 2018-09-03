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
 * Neuron of a self delimiting neural network
 */
public class Neuron {
    public static class OutputNeuronIndexWithWeight {
        public int idx;
        public double weight;
        public boolean mark;

        public OutputNeuronIndexWithWeight(final int idx, final double weight) {
            this.idx = idx;
            this.weight = weight;
        }
    }

    public double threshold;
    public String type;

    public double now;
    public double next;

    public boolean used;

    public List<OutputNeuronIndexWithWeight> outputNeuronIndicesWithWeights = new ArrayList<>();

    // helper for debugging
    public int idx = -1;

    public boolean isFiring() {
        return now > threshold;
    }
}
