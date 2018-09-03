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

package org.opennars.lab.symv2;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.opennars.lab.common.Image2d;
import org.opennars.lab.common.Image2dSampler;
import org.opennars.lab.common.SimpleImage2dSampler;
import org.opennars.lab.symv2.imageactorlayer.ImageActorLayer;
import org.opennars.lab.symv2.imageactorlayer.RetinaFovea;
import org.opennars.lab.symv2.imageactorlayer.RetinaSampler;

public class Test1 {
    public static void main(String[] args) {
        Image2d image = new Image2d(100, 100);

        Image2dSampler sampler = new SimpleImage2dSampler(image, SimpleImage2dSampler.EnumType.LINEAR);

        ImageActorLayer imageActorLayer = new ImageActorLayer(new RetinaSampler(sampler));

        imageActorLayer.fovea = new RetinaFovea();
        imageActorLayer.fovea.position = new BlockRealMatrix(2, 1);
        imageActorLayer.fovea.scale = 64.0f;

        imageActorLayer.spawn();

        for(int iStep=0;iStep<20;iStep++) {
            imageActorLayer.step();
        }
    }
}
