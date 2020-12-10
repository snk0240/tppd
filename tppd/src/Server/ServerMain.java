package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class ServerMain {

    public static final String TIME_REQUEST = "TIME\0";
    public static final int TIMEOUT = 10;


    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }

    public static void main(String[] args) throws UnknownHostException {
        int portUDP;
        int portTCP;
        int portMulti = 5432;
        int portBD = 3306;
        String ipDB;

        if (args.length != 3) {
            //portos de escuta tcp e udp e maquina da sua BD
            System.err.println("The arguments weren't introduced correctly: <UDP port>  <TCP port>  <BD IP>");
            return;
        }
        portUDP = Integer.parseInt("2022"); portTCP = Integer.parseInt("2024"); ipDB = "127.0.0.1";
        System.out.println("UDP port: " + portUDP + "\nTCP port: " + portTCP + "\nBD's ip: " + ipDB + "\n");

        System.out.println("Welcome to Server, write 'exit' to terminate!\n");
        Scanner scan = new Scanner(System.in);

        ServerComm sc = new ServerComm(2022, 2024, "127.0.0.1");
        sc.start();

        /*TCPThreadS tcpt = new TCPThreadS();
        tcpt.start();*/

        while(true){
            System.out.print("> ");
            String msg = scan.next();

            if (msg.equalsIgnoreCase("exit")) {
                /*try {
                    sc.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                break;
            }
        }
    }
}

//Teste BD
            /*try {
                String nome;
                InteracaoDatabase idb = new InteracaoDatabase("localhost:3306");
                Connection con = idb.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM user");
                System.out.println("Users:");
                while (rs.next()) {
                    nome = rs.getString("nome");
                    System.out.println(nome);
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e);
            }*/
//Teste BD

//Rodolfo
            //System.out.println("Welcome to Server, write 'exit' to terminate!\n");
            //Scanner scan = new Scanner(System.in);
            //recebe mensagens da command line
            /*while (true) {
                System.out.print("> ");
                String msg = scan.next();

                if (msg.equalsIgnoreCase("exit")) {
                    s.shutdown();
                    break;
                }
            }*/
//Rodolfo