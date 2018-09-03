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

package org.opennars.lab.symv2.imageactorlayer;

import org.opennars.lab.nn.slim.SelfDelimitingNeuralNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Layer where the Image agents search for features
 */
public class ImageActorLayer {
    private List<ImageActor> activeImageActors = new ArrayList<>();

    private RetinaSampler retinaSampler;

    public RetinaFovea fovea;

    public double rotationDeltaInRadiants = 0.1;
    public double moveDeltaInPixels = 1.0;

    private Random rng = new Random();

    public ImageActorLayer(final RetinaSampler retinaSampler) {
        this.retinaSampler = retinaSampler;
    }

    public void spawn() {
        float gridSize = 8; // grid size in pixels
        int numberOfAgentsInGridCell = 10;

        // TODO< take rotation of fovea into account >

        for(int iy=-(int)(fovea.scale/2.0/gridSize);iy<(int)(fovea.scale/2.0f/gridSize);iy++) {
            for(int ix=-(int)(fovea.scale/2.0/gridSize);ix<(int)(fovea.scale/2.0f/gridSize);ix++) {
                float centerX = ix * gridSize + gridSize/2;
                float centerY = iy * gridSize + gridSize/2;

                for(int i=0;i<numberOfAgentsInGridCell;i++) {
                    float deltaX = rng.nextFloat() * gridSize;
                    float deltaY = rng.nextFloat() * gridSize;

                    float positionX = (float)fovea.position.getEntry(0, 0) + centerX + deltaX;
                    float positionY = (float)fovea.position.getEntry(1, 0) + centerY + deltaY;

                    ImageActor createdAgent = new ImageActor();
                    createdAgent.positionAndOrientation = new ImageActor.PositionAndOrientation();
                    createdAgent.positionAndOrientation.position = new float[]{positionX, positionY};
                    createdAgent.positionAndOrientation.orientationInRadiants = rng.nextFloat() * (float)Math.PI * 2.0f;
                    createdAgent.setup();

                    activeImageActors.add(createdAgent);
                }


            }
        }
    }

    public void step() {
        for(ImageActor iActor : activeImageActors) {
            if(iActor.searchTerminated) {
                // we are only interested in agents which search for proto-edges!
                continue;
            }

            SelfDelimitingNeuralNetwork controlSlimRnn = iActor.controlSlimRnn;

            double[] retinaArray = retinaSampler.sampleRetina(iActor.positionAndOrientation);


            // feed retinaArray to the feed-forward NN and the SLIM-RNN to get the next action

            iActor.feedForwardNeuralNetwork.input = new float[retinaArray.length];
            for(int i=0;i< retinaArray.length;i++) {
                iActor.feedForwardNeuralNetwork.input[i] = (float)retinaArray[i];
            }

            iActor.feedForwardNeuralNetwork.feedforward();

            // feed result of feed forward neural network into SLIM-RNN
            controlSlimRnn.inputVector = new double[iActor.feedForwardNeuralNetwork.outputActivations.length + 1];
            for(int i=0;i<iActor.feedForwardNeuralNetwork.outputActivations.length;i++) {
                controlSlimRnn.inputVector[i] = iActor.feedForwardNeuralNetwork.outputActivations[i].real;
            }

            controlSlimRnn.inputVector[iActor.controlSlimRnn.inputVector.length-1] = 1.0; // constant neuron

            // run SLIM-RNN
            controlSlimRnn.run();

            // do action after result of SLIM-RNN
            final boolean actionRotateLeft = controlSlimRnn.neurons.get(iActor.slimOutputNeuronIdx).now != 0.0;
            final boolean actionRotateRight = controlSlimRnn.neurons.get(iActor.slimOutputNeuronIdx+1).now != 0.0;

            // we move if we don't rotate
            final boolean actionMove = !actionRotateLeft && !actionRotateRight;//controlSlimRnn.neurons.get(iActor.slimOutputNeuronIdx+2).now != 0.0;

            // we terminate if we want to rotate in both directions
            final boolean actionTerminate = actionRotateLeft && actionRotateRight;
                //!actionRotateLeft && !actionRotateRight && !actionMove; // we terminate if we are not moving or rotating

            if (actionTerminate) {
                iActor.searchTerminated = true;
                continue;
            }

            if (actionRotateLeft) {
                iActor.positionAndOrientation.orientationInRadiants += (float)rotationDeltaInRadiants;
            }
            if (actionRotateRight) {
                iActor.positionAndOrientation.orientationInRadiants -= (float)rotationDeltaInRadiants;
            }
            if (actionMove) {
                iActor.positionAndOrientation.position[0] += (Math.cos(iActor.positionAndOrientation.orientationInRadiants) * moveDeltaInPixels);
                iActor.positionAndOrientation.position[1] += (Math.sin(iActor.positionAndOrientation.orientationInRadiants) * moveDeltaInPixels);
            }

        }
    }

}
