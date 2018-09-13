package org.opennars.lab.common.math;

public class DualNumberHelper {
    // see https://slideplayer.com/slide/3431502/#
    public static DualNumber smoothStep(final DualNumber x) {
        if( x.real < 0.0 ) {
            DualNumber res = new DualNumber(0.0);
            res.diff = new double[x.diff.length];
            return res;
        }
        else if( x.real > 1.0 ) {
            DualNumber res = new DualNumber(1.0);
            res.diff = new double[x.diff.length];
            return res;
        }
        else {
            DualNumber _3 = new DualNumber(3.0);
            _3.diff = new double[x.diff.length];
            DualNumber _2 = new DualNumber(2.0);
            _2.diff = new double[x.diff.length];
            DualNumber res = DualNumber.additiveRing(_3, DualNumber.mul(_2, x), -1);
            res = DualNumber.mul(res, x);
            res = DualNumber.mul(res, x);
            return res;
        }
    }
}
