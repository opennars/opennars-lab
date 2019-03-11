package org.opennars.lab.metric;

import org.opennars.main.Nar;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MetricReporter {
    public List<MetricSensor> sensors = new ArrayList<>();

    public String narsVersion = Nar.VERSION;
    public long runId = new Random().nextInt();

    public void connect(String targetHost, int targetPort) throws UnknownHostException {
        receiverTarget = InetAddress.getByName(targetHost);
        receiverTargetPort = targetPort;
    }

    public void sendFromAllSensors() {
        for(final MetricSensor iSensor : sensors) {
            String valueAsString = iSensor.getValueAsString();

            if (valueAsString != null) {
                send(valueAsString, iSensor.getName());
            }
        }

        for(final MetricSensor iSensor : sensors) {
            iSensor.resetAfterSending();
        }
    }

    private void send(final String dataAsString, final String metricPathName) {
        final String timestampAsString = "-1"; // -1 leads to automatic timestamping on arrival of the message

        String metricPath = narsVersion + "_name=" + metricPathName + "_runId=" + runId;

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
}
