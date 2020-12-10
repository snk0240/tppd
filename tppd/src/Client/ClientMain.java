package Client;

import Dados.*;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws UnknownHostException {
        Socket socketServidor;
        if(args.length != 2){
            System.err.println("Sintaxe: java Client <IP_SERVIDOR> <UDP_PORT_SERVIDOR>");
            return;
        }
        System.out.println("Server IP: "+args[0]+"\nServer UDP port: " + args[1] + "\n");

        System.out.println("Welcome to Client, write 'exit' to terminate!\n");
        Scanner scan = new Scanner(System.in);

        ClienteComm cli = new ClienteComm(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
        cli.start();


        while(true){
            System.out.print("> ");
            String msg = scan.next();

            if (msg.equalsIgnoreCase("exit")) {
                cli.shutdown();
                break;
            }
        }
    }
}