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

package org.opennars.lab.common.math;

// see https://en.wikipedia.org/wiki/Automatic_differentiation#Automatic_differentiation_using_dual_numbers
public class DualNumber {
    public double real = 0;
    public double[] diff;

    public DualNumber() {
    }

    public DualNumber(double real) {
        this.real = real;
        this.diff = new double[0];
    }

    // generalization of addition and subtraction because it is in mathematics a additive group
    public static DualNumber additiveRing(final DualNumber left, final DualNumber right, final int mul) {
        assert left.diff.length == right.diff.length;
        assert Math.abs(mul) == 1; // others values are not valid because the multiplication is just used to abstract away the difference between addition and subtraction

        DualNumber res = new DualNumber();
        res.real = left.real + right.real * mul;
        res.diff = new double[left.diff.length];
        for(int i=0;i<left.diff.length;i++)   res.diff[i] = left.diff[i] + right.diff[i] * mul;
        return res;
    }

    public static DualNumber mul(final DualNumber left, final DualNumber right) {
        assert left.diff.length == right.diff.length;

        DualNumber res = new DualNumber();
        res.real = left.real * right.real;
        res.diff = new double[left.diff.length];
        for(int i=0;i<left.diff.length;i++)   res.diff[i] = left.real*right.diff[i] + left.diff[i]*right.real;
        return res;
    }

    public static DualNumber exp(final DualNumber val) {
        DualNumber res = new DualNumber();
        res.real = Math.exp(val.real);
        res.diff = new double[val.diff.length];
        for(int i=0;i<val.diff.length;i++)   res.diff[i] = val.diff[i] * Math.exp(val.real);
        return res;
    }

    public static DualNumber max(final DualNumber a, final DualNumber b) {
        return a.real > b.real ? a : b;
    }
}
