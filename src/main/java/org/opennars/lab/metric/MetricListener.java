package org.opennars.lab.metric;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener after GOF for metric events
 */
public class MetricListener {
    public void register(MetricObserver obs) {
        observers.add(obs);
    }

    public void notifyObservers(String name, int value) {
        for(MetricObserver iObserver:observers) {
            iObserver.notifyInt(name, value);
        }
    }

    public List<MetricObserver> observers = new ArrayList<>();
}
