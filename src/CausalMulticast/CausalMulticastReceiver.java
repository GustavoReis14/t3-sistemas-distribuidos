package CausalMulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.SocketException;

public class CausalMulticastReceiver extends Thread{
    private ICausalMulticastReceiver listener;
    private DatagramSocket datagramSocket;

    public CausalMulticastReceiver(ICausalMulticastReceiver listener) throws SocketException {
        this.listener = listener;
        datagramSocket = new DatagramSocket(CausalMulticast.UNICAST_PORT);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[9999];

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
                datagramSocket.receive(packet);
                listener.messageReceived(ChanelMessage.deserialize(packet.getData()), packet.getAddress());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
