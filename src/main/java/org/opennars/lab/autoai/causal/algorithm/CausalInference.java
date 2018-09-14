package org.opennars.lab.autoai.causal.algorithm;

import java.util.*;

/** finds a new random linearization
 *
 * written after Causal Inference AI theory http://sergio.pissanetzky.com/
 *
 * @author Robert Wünsche
 */
public class CausalInference extends AbstractCausalInference {

    private Map<Integer, List<Integer>> mustPrecede = new HashMap<>();
    public int numberOfNodes = 0;

    private List<Integer> openSet = new ArrayList<>();

    // set of nodes which we already did
    private Set<Integer> doneSet = new HashSet<>();

    private Random rng = new Random();

    public CausalInference() {
        reset();
    }


    public void build() {
        buildMustPrecede();
        fillOpenSet();
    }

    private void fillOpenSet() {
        openSet.clear();

        for (int iKey = 0; iKey < numberOfNodes; iKey++) {
            if (mustPrecede.get(iKey).size() == 0) {
                openSet.add(iKey);
            }
        }

        int debugHere = 1;
    }

    private void buildMustPrecede() {
        mustPrecede.clear();

        for (int iKey=0;iKey<numberOfNodes;iKey++) {
            mustPrecede.put(iKey, new ArrayList<>());
        }

        for(int iKey : followup.keySet()) {
            for(int iValue : followup.get(iKey)) {
                mustPrecede.get(iValue).add(iKey);
            }
        }

        int debugHere = 1;
    }

    public void reset() {
        doneSet.clear();
        currentLinearization.clear();
    }

    /**
     * the energy has to be calculated after finishing!
     *
     * @return true if finished
     */
    public boolean step() {
        if (openSet.size() == 0) {
            return true;
        }

        // take out random element from openSet
        Integer currentElement = null;
        int openSetIdx = 0;
        while (true) {
            openSetIdx = rng.nextInt(openSet.size());
            currentElement = openSet.get(openSetIdx);

            // check if we did all preceding elements
            //print(currentElement)
            //print(list(self.mustPrecede.keys()))
            if (CausalSetHelpers.inSet(doneSet, mustPrecede.get(currentElement))) {
                break;
            }
        }


        openSet.remove(openSetIdx);

        final List<Integer> followupNodes = followup.get(currentElement);

		// add to linearized solution
        currentLinearization.add(currentElement);

		// to indicate that we can't choose it again
        doneSet.add(currentElement);

        if (followupNodes != null) {
            for (int iFollowup : followupNodes) {
                if (!doneSet.contains(iFollowup) && !openSet.contains(iFollowup)) {
                    openSet.add(iFollowup);
                }
            }
        }

        return false;
    }

    public int calcEnergy(final List<Integer> linearization) throws CausalSetHelpers.NotValid {
        return CausalSetHelpers.calcEnergy(followup, linearization);
    }
}
