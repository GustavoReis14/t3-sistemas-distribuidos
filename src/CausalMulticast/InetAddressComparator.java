package CausalMulticast;

import java.net.InetAddress;
import java.util.Comparator;

public class InetAddressComparator implements Comparator<InetAddress> {
    @Override
    public int compare(InetAddress o1, InetAddress o2) {
        return o1.getHostAddress().compareTo(o2.getHostAddress());
    }
}
