package org.opennars.lab.autoai.causal.algorithm;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CausalSetHelpers {
    public static boolean inSet(final Set<Integer> set, final List<Integer> checkedList) {
        for (int iChecked : checkedList) {
            if (!set.contains(iChecked)) {
                return false;
            }
        }

        return true;
    }

    public static int calcEnergy(Map<Integer, List<Integer>> followup, final List<Integer> linearization) throws NotValid {
        int e = 0;

        int idx = 0;

        for (final int i : linearization) {
            final List<Integer> followupNodes = followup.get(i);
            //print("followup " + str(followupNodes))

            if (followupNodes != null) {
                for (int iFollowupNode : followupNodes) {
                    final int idxOfFollowupNode = retIndexOfElementAfterIdx(linearization, idx, iFollowupNode);
                    e += (idxOfFollowupNode - idx - 1);
                }
            }

            idx++;
        }

        return e;
    }

    private static int retIndexOfElementAfterIdx(final List<Integer> linearization, final int startIdx, final int element) throws NotValid {
        for (int idx=startIdx + 1;idx<linearization.size();idx++) {
            if (linearization.get(idx) == element) {
                return idx;
            }
        }

        throw new NotValid();
    }

    /**
     * Exception if the linearzation was not valid
     */
    public static class NotValid extends Exception {
    }
}
