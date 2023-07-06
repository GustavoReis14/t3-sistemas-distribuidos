package CausalMulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

public class CausalMulticastDiscovery extends Thread {
    private static final String helloMessage = "JOINING_GROUP";
    private ArrayList<InetAddress> discoveredIpAddresses = new ArrayList<>();;
    private MulticastSocket multicastSocket;
    private InetAddress group;
    private InetAddressComparator comparator;

    public List<InetAddress> getDiscoveredIpAddresses() {
        return discoveredIpAddresses;
    }

    public CausalMulticastDiscovery() throws IOException {
        try {
            multicastSocket = new MulticastSocket(CausalMulticast.MULTICAST_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        comparator = new InetAddressComparator();
        group = InetAddress.getByName(CausalMulticast.MULTICAST_ADDRESS);
    }

    @Override
    public void run() {
        byte[] buf = new byte[56];

        try {
            multicastSocket.joinGroup(group);

            sendHelloMessage();

            while(true) {
                DatagramPacket receivedMessage = new DatagramPacket(buf, buf.length);
                multicastSocket.receive(receivedMessage);
                String messageContent = new String(receivedMessage.getData(), 0, receivedMessage.getLength(), "UTF-8");

                if(messageContent.equals(helloMessage)) {
                    synchronized (this) {
                        InetAddress client = receivedMessage.getAddress();
                        boolean added = false;

                        for(InetAddress address : discoveredIpAddresses) {
                            if(address.equals(client)) {
                                added = true;
                            }
                        }

                        if(!added) {
                            System.out.println(String.format("Middleware - Cliente Conectado ", client.getHostAddress()));

                            discoveredIpAddresses.add(client);
                            discoveredIpAddresses.sort(comparator);

                            sendHelloMessage();
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();

            System.out.println("Problema no Discovery ");
        }
    }

    private void sendHelloMessage() throws IOException {
        DatagramPacket helloDatagramPacket = new DatagramPacket(helloMessage.getBytes("UTF-8"), helloMessage.length(), group, CausalMulticast.MULTICAST_PORT);

        multicastSocket.send(helloDatagramPacket);
    }
}
