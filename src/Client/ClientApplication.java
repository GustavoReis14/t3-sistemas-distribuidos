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

            if(input.toLowerCase().equals("sync")) {
                middleware.sendAllNotSentMessages();
            }
            else if(input.length() == 0) {
                System.out.println("MENSAGEM INVALIDA");
            }
            else if(middleware.hasDelayedMessages()) {
                System.out.println("NÃO PODE SER ENTREGUE POIS EXISTEM MENSAGENS COM DELAY");
            }
            else {
                middleware.mcsend(input, this);
            }
        }
    }

    @Override
    public void deliver(String msg) {
        System.out.println("Mensagem recebida - " + msg);
    }
}
