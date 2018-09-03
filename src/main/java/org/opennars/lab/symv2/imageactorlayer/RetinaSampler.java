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

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.opennars.lab.common.Image2dSampler;

/**
 * Samples the retina
 */
public class RetinaSampler {
    public Image2dSampler sampler;

    public RetinaSampler(final Image2dSampler sampler) {
        this.sampler = sampler;
    }

    public double[] sampleRetina(ImageActor.PositionAndOrientation positionAndOrientation) {
        double[] m=new double[3*3 + (3*3-1)];

        int idx=0;

        for(int y=-1;y<=1;y++) {
            for(int x=-1;x<=1;x++) {
                RealMatrix rel = new BlockRealMatrix(2, 1);
                rel.setEntry(0, 0, x);
                rel.setEntry(1, 0, y);

                RealMatrix rotated = createRotationMatrix(positionAndOrientation.orientationInRadiants).multiply(rel);

                final double xAbs = rotated.getEntry(0, 0) + positionAndOrientation.position[0];
                final double yAbs = rotated.getEntry(1, 0) + positionAndOrientation.position[1];

                m[idx++] = sampler.sampleAt(yAbs, xAbs);
            }
        }

        // second order pixels
        for(int deltaY=-1;deltaY<=1;deltaY++) {
            for(int deltaX=-1;deltaX<=1;deltaX++) {
                // ignore center region because it is already sampled
                if(deltaX==0&&deltaY==0) {
                    continue;
                }

                m[idx++] = integrateSamples(positionAndOrientation.position[0], positionAndOrientation.position[1], deltaX, deltaY, 3, positionAndOrientation.orientationInRadiants);
            }
        }

        return m;
    }

    /**
     * @param centerX
     * @param centerY
     * @param deltaX
     * @param deltaY
     * @param size is total integrated size
     * @return
     */
    private double integrateSamples(final double centerX, final double centerY, final int deltaX, final int deltaY, final int size, final double theta) {
        double integratedResult = 0.0;
        final int pixelRadius = size == 1 ? 0 : 1;

        for(int offsetY=-pixelRadius;offsetY<=pixelRadius;offsetY++) {
            for(int offsetX=-pixelRadius;offsetX<=pixelRadius;offsetX++) {
                RealMatrix rel = new BlockRealMatrix(2, 1);
                rel.setEntry(0, 0,  deltaX*size+offsetX);
                rel.setEntry(1, 0, deltaY*size+offsetY);

                RealMatrix rotated = createRotationMatrix(theta).multiply(rel);

                final double xAbs = rotated.getEntry(0, 0) + centerX;
                final double yAbs = rotated.getEntry(1, 0) + centerY;

                integratedResult += sampler.sampleAt(yAbs, xAbs);
            }
        }
        integratedResult /= (size*size);
        return integratedResult;
    }


    private static RealMatrix createRotationMatrix(final double theta) {
        // https://en.wikipedia.org/wiki/Rotation_matrix
        RealMatrix transformationMatrix = new BlockRealMatrix(2, 2);
        transformationMatrix.setEntry(0, 0, Math.cos(theta));
        transformationMatrix.setEntry(0, 1, -Math.sin(theta));
        transformationMatrix.setEntry(1, 0, Math.sin(theta));
        transformationMatrix.setEntry(1, 1, Math.cos(theta));
        return transformationMatrix;
    }

    /*
    private static RealMatrix createTranslationMatrix(final double x, final double y) {
        // https://en.wikipedia.org/wiki/Transformation_matrix#Affine_transformations
        RealMatrix transformationMatrix = new BlockRealMatrix(3, 3);
        transformationMatrix.setEntry(0, 2, x);
        transformationMatrix.setEntry(1, 2, y);
        transformationMatrix.setEntry(0, 0, 1);
        transformationMatrix.setEntry(1, 1, 1);
        transformationMatrix.setEntry(2, 2, 1);
        return transformationMatrix;
    }
     */
}
