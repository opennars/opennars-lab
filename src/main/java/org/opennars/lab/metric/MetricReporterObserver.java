package org.opennars.lab.metric;

/**
 * observer which reports the metric as an UDP message
 */
public class MetricReporterObserver extends MetricObserver {
    private final MetricReporter reporter;

    public MetricReporterObserver(MetricListener listener, MetricReporter reporter) {
        super(listener);
        this.reporter = reporter;
    }

    @Override
    public void notifyInt(String name, int value) {
        reporter.notifyInt(name, value);
    }
}
