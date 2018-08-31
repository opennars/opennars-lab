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

package org.opennars.lab.common;

/**
 * Image which can be manipulated
 */
public class Image2d {
    final protected int width;
    protected float[] arr;

    public Image2d(final int height, final int width) {
        arr = new float[width*height];
        this.width = width;
        clear();
    }

    public int retWidth() {
        return width;
    }

    public int retHeight() {
        return arr.length/width;
    }

    public void clear() {
        for (int y = 0; y < this.retHeight(); y++) {
            for (int x = 0; x < this.retWidth(); x++) {
                setAt(y, x, 0);
            }
        }
    }

    public double retAt(final int y, final int x) {
        return arr[y * width + x];
    }

    public double retAtUnbound(final int y, final int x) {
        if(y<0||y>=this.retHeight()||x<0||x>=this.retWidth()) {
            return 0.0;
        }
        return this.retAt(y,x);
    }

    public void setAt(final int y, final int x, final double value) {
        arr[y * width + x] = (float)value;
    }

    public void setAtUnbound(final int y, final int x, final double value) {
        if(y<0||y>=retHeight()||x<0||x>=retWidth()) {
            return;
        }
        setAt(y,x, value);
    }
}
