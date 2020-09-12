/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
