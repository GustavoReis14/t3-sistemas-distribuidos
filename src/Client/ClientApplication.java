package Client;

import CausalMulticast.CausalMulticast;
import CausalMulticast.ICausalMulticast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientApplication implements ICausalMulticast {
    private CausalMulticast middleware = new CausalMulticast(this);

    public ClientApplication() throws IOException {
    }


    public void start() throws IOException {
        middleware.start();

        Scanner scanner = new Scanner(System.in);

        while(true) {
            String input = scanner.nextLine();

            if(input.equals("send")) {
                middleware.sendAllNotSentMessages();
            }
            else if(input.length() == 0 || input.charAt(0) != '>') {
                System.out.println("[WARN] Unrecognized command. Messages should start with '>'");
            }
            else if(middleware.hasDelayedMessages()) {
                System.out.println("[ERROR] Cannot send messages having delayed ones.");
            }
            else {
                middleware.mcsend(input.substring(1));
            }
        }
    }

    @Override
    public void deliver(String msg) {
        System.out.println("Mensagem recebida - " + msg);
    }
}
