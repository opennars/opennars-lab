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
