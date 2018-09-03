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
package org.opennars.lab.symv2.layer2;

import org.opennars.lab.common.Image2d;

/**
 * renderer to draw for this layer
 */
public class Renderer {
    public static void drawLine(Image2d image, final int x0, final int y0, final int x1, final int y1) {
        // bresenham algorithm
        // algorithm heavily inspired by http://www.k-achilles.de/algorithmen/bresenham-gerade.pdf
        // we just use some array trickery to get rid of the duplicated code

        int[] d, /* d times two */d2,  step, current, end;

        d = new int[2];
        d2 = new int[2];
        current = new int[2];
        end = new int[2];
        step = new int[2];

        d[0] = Math.abs(x1-x0); step[0] = x0<x1 ? 1 : -1;
        d[1] = Math.abs(y1-y0); step[1] = y0<y1 ? 1 : -1;
        d2[0] = 2*d[0];
        d2[1] = 2*d[1];

        current[0] = x0;
        current[1] = y0;
        end[0] = x1;
        end[1] = y1;

	    final int xArrIndex = (d[1] <= d[0]) ? 0 : 1;

        int f = -d[xArrIndex];

        while( current[xArrIndex] != end[xArrIndex] ) {
            image.setAt(current[1], current[0], 1.0);

            f += d2[1-xArrIndex];

            if( f > 0 ) {
                current[1-xArrIndex] += step[1-xArrIndex];
                f -= d2[xArrIndex];
            }
            current[xArrIndex] += step[xArrIndex];
        }

        image.setAt(end[1], end[0], 1.0);
    }
}
