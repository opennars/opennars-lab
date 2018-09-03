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
 * Samples unfiltered or linear filtered
 */
public class SimpleImage2dSampler implements Image2dSampler {
    private final Image2d image;
    private final EnumType type;

    public SimpleImage2dSampler(final Image2d image, final EnumType type) {
        this.image = image;
        this.type = type;
    }

    @Override
    public double sampleAt(final double y, final double x) {
        if (type == EnumType.NEAREST) {
            return image.retAtUnbound((int)y, (int)x);
        }
        else if(type == EnumType.LINEAR) {
            final double floorX = Math.floor(x);
            final double floorY = Math.floor(y);

            final double _00 = image.retAtUnbound((int)y, (int)x);
            final double _01 = image.retAtUnbound((int)y, (int)x+1);
            final double _10 = image.retAtUnbound((int)y+1, (int)x);
            final double _11 = image.retAtUnbound((int)y+1, (int)x+1);

            final double _0 = lerp(_00, _01, floorX);
            final double _1 = lerp(_10, _11, floorX);

            return lerp(_0, _1, floorY);

        }

        return 0; // we just ignore that the type is invalid
    }

    // helper
    private static double lerp(final double a, final double b, final double t) {
        return b*t + (t-1.0)*a;
    }

    public enum EnumType {
        NEAREST,
        LINEAR,
        // CUBIC, // TODO
    }
}
