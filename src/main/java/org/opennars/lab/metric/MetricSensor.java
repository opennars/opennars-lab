package org.opennars.lab.metric;

public interface MetricSensor {
    String getName();

    String getValueAsString();

    /**
     * is called to give the sensor a chance to reset the stat after sending
     */
    void resetAfterSending();
}
