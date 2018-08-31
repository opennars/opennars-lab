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
    public double sampleAt(double y, double x) {
        if (type == EnumType.NEAREST) {
            return image.retAtUnbound((int)y, (int)x);
        }

        return 0; // we just ignore that the type is invalid
    }

    public enum EnumType {
        NEAREST,
        // LINEAR, // TODO
        // CUBIC, // TODO
    }
}
