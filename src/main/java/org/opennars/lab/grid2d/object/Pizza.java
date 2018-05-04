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
package org.opennars.lab.grid2d.object;

import java.awt.Color;
import org.opennars.lab.grid2d.main.Effect;
import org.opennars.lab.grid2d.main.LocalGridObject;

/**
 *
 * @author me
 */
public class Pizza extends LocalGridObject {

    public Pizza(int x, int y, String doorname) {
        super(x, y);
        this.doorname = doorname;
    }

    @Override
    public void update(Effect nextEffect) {
    }
    float animationLerpRate = 0.5f; //LERP interpolation rate

    @Override
    public void draw() {
        cx = (cx * (1.0f - animationLerpRate)) + (x * animationLerpRate);
        cy = (cy * (1.0f - animationLerpRate)) + (y * animationLerpRate);
        cheading = (cheading * (1.0f - animationLerpRate / 2.0f)) + (heading * animationLerpRate / 2.0f);
        float scale = (float) Math.sin(Math.PI / 7f) * 0.05f + 1.0f;
        space.pushMatrix();
        space.translate(cx, cy);
        space.pushMatrix();
        space.scale(scale * 0.8f);
        space.fill(Color.ORANGE.getRGB(), 255);
        space.ellipse(0, 0, 1.0f, 1.0f);
        space.fill(Color.YELLOW.getRGB(), 255);
        space.ellipse(0, 0, 0.8f, 0.8f);
        
        space.popMatrix();
        if (!"".equals(doorname)) {
            space.textSize(0.2f);
            space.fill(255, 0, 0);
            space.pushMatrix();
            space.text(doorname, 0, 0);
            space.popMatrix();
        }
        
        //eyes
        space.fill(Color.RED.getRGB(), 255);
        space.rotate((float)(Math.PI/180f * cheading));
        space.ellipse(-0.15f,0.2f,0.1f,0.1f);
        space.ellipse(0.15f,0.2f,0.1f,0.1f);
        space.ellipse(-0.2f,-0.2f,0.1f,0.1f);
        space.ellipse(0.2f,-0.2f,0.1f,0.1f);
        space.ellipse(0.0f,-0.0f,0.1f,0.1f);
        
        space.popMatrix();
    }
}
