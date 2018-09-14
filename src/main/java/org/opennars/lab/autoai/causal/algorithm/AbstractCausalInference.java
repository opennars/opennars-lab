package org.opennars.lab.autoai.causal.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for all implementations of the causal inference theory
 *
 * written after Causal Inference AI theory http://sergio.pissanetzky.com/
 *
 * @author Robert Wünsche
 */
public abstract class AbstractCausalInference {
    public Map<Integer, List<Integer>> followup = new HashMap<>();

    public List<Integer> currentLinearization = new ArrayList<>();
}

