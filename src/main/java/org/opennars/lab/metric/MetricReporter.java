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

import org.opennars.main.Nar;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * reports metric to a receiver over UDP (for graphite)
 */
public class MetricReporter {
    public String narsVersion = Nar.VERSION;
    public String narsName = Nar.NAME;
    public long runId = Calendar.getInstance().getTimeInMillis();

    public List<MetricObserver> observers = new ArrayList<>();


    private Map<String, Integer> integerMap = new HashMap<>();

    public void connect(String targetHost, int targetPort) throws UnknownHostException {
        receiverTarget = InetAddress.getByName(targetHost);
        receiverTargetPort = targetPort;
    }

    public void notifyInt(String name, int value) {
        integerMap.put(name, value);

        //pushReport();
        send(Integer.toString(value), name);
    }

    private void pushReport() {
        for (Map.Entry<String, Integer> iEntry: integerMap.entrySet()) {
            send(Integer.toString(iEntry.getValue()), iEntry.getKey());
        }
    }


    private void send(final String dataAsString, final String metricPathName) {
        String narsVersionSerialized = narsVersion.replace('.', '_').replaceFirst("\\ ", "."); // required for graphite
        String metricPath = narsName + "." + narsVersionSerialized + "." +runId+ "." + metricPathName;

        String payload = metricPath +":"+ dataAsString + "|c" + "\n";

        final byte[] serializedPayload = payload.getBytes();

        final DatagramPacket packet = new DatagramPacket(serializedPayload, serializedPayload.length, receiverTarget, receiverTargetPort);
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.send(packet);
        } catch (SocketException e) {
            // unlikely to happen - ignored
        } catch (IOException e) {
            // unlikely to happen - ignored
        }
    }

    private InetAddress receiverTarget;
    private int receiverTargetPort;

    public void register(MetricObserver metricObserver) {
        observers.add(metricObserver);
    }
}
