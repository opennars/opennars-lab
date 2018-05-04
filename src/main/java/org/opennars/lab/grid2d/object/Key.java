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


public class Key extends LocalGridObject {

    
    
    public Key(int x, int y, String doorname) {
        super(x, y);
        this.doorname=doorname;
    }

    @Override
    public void update(Effect nextEffect) {
    }

    @Override
    public void draw() {
        float scale = (float)Math.sin(space.getTime()/7f)*0.05f + 1.0f;
        float a = space.getTime()/10;
        
        space.pushMatrix();
        space.translate(cx, cy);
        
        space.pushMatrix();
        space.rotate(a);
        space.scale(scale*0.8f);
        
        space.fill(Color.GREEN.getRGB());
        space.rect(-0.4f, -0.15f/2, 0.8f, 0.15f);
        space.rect(-0.5f, -0.2f, 0.3f, 0.4f);
        space.rect(0.3f, 0, 0.1f, 0.15f);
        space.rect(0.1f, 0, 0.1f, 0.15f);
        space.popMatrix();
        if(!"".equals(doorname))
        {
            space.textSize(0.2f);
            space.fill(255,0,0);
            space.pushMatrix();
            space.text(doorname,0,0);
            space.popMatrix();
        }
        
        space.popMatrix();

    }
    
    
}
