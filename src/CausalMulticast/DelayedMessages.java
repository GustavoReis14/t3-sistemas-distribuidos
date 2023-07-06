package CausalMulticast;

import java.net.InetAddress;

public class DelayedMessages {
    private ChanelMessage message;
    private InetAddress sender;

    public DelayedMessages(ChanelMessage message, InetAddress sender) {
        this.message = message;
        this.sender = sender;
    }

    public ChanelMessage getMessage() {
        return message;
    }

    public InetAddress getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return String.format("{message=%s, sender=%s}", message, sender);
    }
}
