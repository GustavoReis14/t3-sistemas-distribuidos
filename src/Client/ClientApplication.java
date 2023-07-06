package Client;

import CausalMulticast.CausalMulticast;
import CausalMulticast.ICausalMulticast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ClientApplication implements ICausalMulticast {
    private CausalMulticast middleware = new CausalMulticast(this);


    public void start() {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            String input = scanner.nextLine();


        }
    }

    @Override
    public void deliver(String msg) {
        System.out.println("Mensagem recebida - " + msg);
    }
}
