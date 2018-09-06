package org.opennars.lab.autoai;

import java.util.ArrayList;
import java.util.List;

public class Executive {
    public List<RecordedOperation> recordedOperations = new ArrayList<>();

    // called by op to record a add
    public void opAdd(String res, String a, String b) {
        // record add
        recordedOperations.add(new RecordedOperation(RecordedOperation.EnumType.ADD, a, b, res));
    }

    // called by op to record a vectorized multiply-add
    public void opMadd(String res, String a, String b) {
        // record madd
        recordedOperations.add(new RecordedOperation(RecordedOperation.EnumType.MADD, a, b, res));
    }

    // called by op to record a vectorized multiply-add
    public void opMaddv(String res, String a, String b) {
        // record madd
        recordedOperations.add(new RecordedOperation(RecordedOperation.EnumType.MADDV, a, b, res));
    }

    public static class RecordedOperation {
        public final EnumType type;
        public final String a, b, res;

        public RecordedOperation(final EnumType type, final String a, final String b, final String res) {
            this.type = type;
            this.a = a;
            this.b = b;
            this.res = res;
        }

        public enum EnumType {
            ADD, // add
            MADDV, // vectorized madd
            MADD, // multiply add
        }

    }
}
