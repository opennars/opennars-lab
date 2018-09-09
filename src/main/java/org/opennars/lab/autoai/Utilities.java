package org.opennars.lab.autoai;

public class Utilities {
    // helper to generate a vector where a index is set to 1
    public static double[] makeArrWithOnehot(final int size, final int onehotIndex) {
        double[] result = new double[size];
        result[onehotIndex] = 1;
        return result;
    }
}
