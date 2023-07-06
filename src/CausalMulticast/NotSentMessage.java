package CausalMulticast;

import java.net.InetAddress;

public class NotSentMessage {
    private byte[] serializedMessage;
    private InetAddress destination;

    public NotSentMessage(byte[] serializedMessage, InetAddress destination) {
        this.serializedMessage = serializedMessage;
        this.destination = destination;
    }

    public byte[] getSerializedMessage() {
        return serializedMessage;
    }

    public InetAddress getDestination() {
        return destination;
    }
}
