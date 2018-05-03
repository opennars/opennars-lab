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
package nars.lab.grid2d.main;

import static nars.lab.grid2d.main.Hauto.DOWN;
import static nars.lab.grid2d.main.Hauto.LEFT;
import static nars.lab.grid2d.main.Hauto.RIGHT;
import static nars.lab.grid2d.main.Hauto.UP;

/**
 * GridObject with a specific position
 */
public abstract class LocalGridObject implements GridObject {

    public float cx, cy, cheading; //current drawn location, for animation
    
    public String doorname="";
    public int x;
    public int y;
    public int heading; //in degrees
    public Grid2DSpace space;

    public LocalGridObject(int x, int y) {
        setPosition(x, y);
    }
    
    /** set by space when added */
    @Override
    public void init(Grid2DSpace space) {
        this.space = space;
    }
    
    public void setPosition(int x, int y) {
        this.cx = this.x = x;
        this.cy = this.y = y;
    }
    
    public int x() { return x; }
    public int y() { return y; }    
    
    
    public static int angle(int targetAngle) {
        while (targetAngle > 180) targetAngle-=360;
        while (targetAngle <= -180) targetAngle+=360;
        return targetAngle;
    }
    
    /** cell currently standing on */
    public Cell cellOn() {
        return space.cells.at(x, y);
    }
    
    public Cell cellAbsolute(int targetAngle) {
        int tx = x;
        int ty = y;
        switch (angle(targetAngle)) {
            case UP:
                ty++;
                break;
            case DOWN:
                ty--;
                break;
            case LEFT:
                tx--;
                break;
            case RIGHT:
                tx++;
                break;
            default:
                System.err.println("cellAbsolute(" + targetAngle + " from " + heading + ") = Invalid angle: " + targetAngle);
                return null;
        }
        return space.cells.at(tx, ty);
    }
    /**
     * @return 
     */
    public Cell cellRelative(int dAngle) {
        int targetAngle = angle(heading + dAngle);
        return cellAbsolute(targetAngle);
    }
}
