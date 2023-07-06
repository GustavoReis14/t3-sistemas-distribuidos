package CausalMulticast;

import Client.ClientApplication;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CausalMulticast implements ICausalMulticastReceiver {
    public static final String MULTICAST_ADDRESS = "225.0.0.0";
    public static final int MULTICAST_PORT = 15050;
    public static final int UNICAST_PORT = 15051;

    private InetAddress currentAddress;
    private DatagramSocket datagramSocket;
    private ICausalMulticast client;
    private CausalMulticastDiscovery discovery;
    private CausalMulticastReceiver receiver;
    private int[] clockArray;
    private int indexOfInstanceInClockArray;
    private ArrayList<DelayedMessages> delayedMessages;
    private ArrayList<NotSentMessage> notSentMessages;


    public CausalMulticast(ClientApplication client) throws IOException {
        this.currentAddress = InetAddress.getLocalHost();
        this.datagramSocket = new DatagramSocket();
        this.client = client;
        this.delayedMessages = new ArrayList<>();
        this.notSentMessages = new ArrayList<>();
        this.discovery = new CausalMulticastDiscovery();
        this.receiver = new CausalMulticastReceiver(this);
        this.indexOfInstanceInClockArray = -1;
    }

    public void start() {
        System.out.println("Middleware iniciado na porta " + this.currentAddress);
        receiver.start();
        discovery.start();
    }

    public void mcsend(String msg, ICausalMulticast client) throws IOException {
        if(indexOfInstanceInClockArray < 0) {
            initializeClockArray();
        }

        String[] splitMessage = msg.split("-");
        ChanelMessage chanelMessage = new ChanelMessage(splitMessage.length > 1 ? splitMessage[1] : msg, clockArray);
        byte[] serializedMessage = chanelMessage.serialize();
        List<InetAddress> toSend = new ArrayList<>();

        for(InetAddress address : discovery.getDiscoveredIpAddresses()) {

            if(splitMessage[0].toLowerCase().equals("delay")) {
                notSentMessages.add(new NotSentMessage(serializedMessage, address));
            }
            else {
                toSend.add(address);
            }
        }

        for(InetAddress address : toSend) {
            DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, address, UNICAST_PORT);
            datagramSocket.send(packet);
        }

        clockArray[indexOfInstanceInClockArray]++;
    }

    private void initializeClockArray() {
        List<InetAddress> allIps = discovery.getDiscoveredIpAddresses();

        this.clockArray = new int[allIps.size()];
        this.indexOfInstanceInClockArray = getIndexForAddress(currentAddress);

        for(int i = 0; i < allIps.size(); i++) {
            clockArray[i] = 0;
        }
    }

    private int getIndexForAddress(InetAddress address) {
        List<InetAddress> allIps = discovery.getDiscoveredIpAddresses();

        for(int i = 0; i < allIps.size(); i++) {
            if(allIps.get(i).getHostAddress().equals(address.getHostAddress())) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void messageReceived(ChanelMessage message, InetAddress sender) {
        synchronized (this) {
            if(indexOfInstanceInClockArray < 0) {
                initializeClockArray();
            }

            System.out.println("Middleware - Mensagem recebida: "+message.toString()+"\t Clock: "+ Arrays.toString(clockArray));

            if(!messageCanBeDelivered(message)) {
                delayedMessages.add(new DelayedMessages(message, sender));
                System.out.printf("%s - Mensagem %s foi atrasada. delay: %s\n", sender.getHostAddress(), message.getContent(), Arrays.toString(delayedMessages.toArray()));
                return;
            }

            deliverMessage(message, sender);

            checkDelayedMessages();
        }
    }

    private boolean messageCanBeDelivered(ChanelMessage message) {
        for(int i = 0; i < clockArray.length; i++) {
            if(message.getClockArray()[i] > clockArray[i]) {
                return false;
            }
        }

        return true;
    }

    private void deliverMessage(ChanelMessage message, InetAddress sender) {
        int indexOfSource = getIndexForAddress(sender);

        if(indexOfSource != indexOfInstanceInClockArray) {
            clockArray[indexOfSource]++;
        }

        client.deliver(message.getContent());
    }

    private void checkDelayedMessages() {
        for(int i = 0; i < delayedMessages.size(); i++) {
            DelayedMessages delayedMessage = delayedMessages.get(i);

            if(messageCanBeDelivered(delayedMessage.getMessage())) {
                deliverMessage(delayedMessage.getMessage(), delayedMessage.getSender());
                delayedMessages.remove(i);

                checkDelayedMessages();
                break;
            }
        }
    }

    public void sendAllNotSentMessages() throws IOException {
        synchronized (this) {
            while(notSentMessages.size() > 0) {
                NotSentMessage message = notSentMessages.get(0);
                byte[] messageBytes = message.getSerializedMessage();
                DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, message.getDestination(), UNICAST_PORT);

                System.out.println(String.format("Delay - Mensagem entregue %s", message.getDestination().getHostAddress()));

                datagramSocket.send(packet);
                notSentMessages.remove(0);
            }
        }
    }

    public boolean hasDelayedMessages() {
        return delayedMessages.size() > 0;
    }

}
