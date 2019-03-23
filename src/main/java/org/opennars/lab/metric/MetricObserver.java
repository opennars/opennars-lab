package org.opennars.lab.metric;

/**
 * GOF observer for metrics
 */
public abstract class MetricObserver {
    public MetricObserver(MetricListener listener) {
        listener.register(this);
    }

    abstract public void notifyInt(String name, int value);
}
