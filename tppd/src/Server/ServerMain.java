package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class ServerMain {
    public static final String EXIT = "EXIT";

    public static void main(String[] args) {
        int portUDP;
        int portTCP;
        String ipDB;

        if (args.length != 3) {
            //portos de escuta tcp e udp e maquina da sua BD
            System.err.println("The arguments weren't introduced correctly: <UDP port>  <TCP port>  <BD IP>");
            return;
        }
        try {
            portUDP = Integer.parseInt(args[0]);
            portTCP = Integer.parseInt(args[1]);
            ipDB = args[2];
            System.out.println("UDP port: " + portUDP + "\nTCP port: " + portTCP + "\nBD's ip: " + ipDB + "\n");

            ServerComm s = new ServerComm(portUDP, portTCP, ipDB);
            s.start();

            System.out.println("Welcome to Server, write 'exit' to terminate!\n");
            Scanner scan = new Scanner(System.in);

            //recebe mensagens da command line
            while (true) {
                System.out.print("> ");
                String msg = scan.next();

                if (msg.equalsIgnoreCase(EXIT)) {
                    s.shutdown();
                    break;
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("The BD port should be an unsigned int:\t" + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}