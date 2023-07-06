package CausalMulticast;

import java.net.InetAddress;

public interface ICausalMulticastReceiver {
    void messageReceived(ChanelMessage message, InetAddress sender);
}