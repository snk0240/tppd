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

    public static void main(String[] args) {
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
        try {
            portUDP = Integer.parseInt(args[0]);
            portTCP = Integer.parseInt(args[1]);
            ipDB = args[2];
            System.out.println("UDP port: " + portUDP + "\nTCP port: " + portTCP + "\nBD's ip: " + ipDB + "\n");

            ServerComm s = new ServerComm(portUDP, portTCP, ipDB);
            s.start();

            System.out.println("Welcome to Server, write 'exit' to terminate!\n");
            Scanner scan = new Scanner(System.in);
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
//Teste conexao cli
            try{
                byte[] data1 = new byte[1024];
                DatagramPacket inputPacket = new DatagramPacket(data1, data1.length);
                System.out.println("Esperando 1 conexao");
                s.datagramSocket.receive(inputPacket);

                String receivedData = new String(inputPacket.getData());
                System.out.println("Enviado: "+receivedData);
                byte[] data2 = Integer.toString(portTCP).getBytes();

                InetAddress senderAddress = inputPacket.getAddress();
                int senderPort = inputPacket.getPort();

                DatagramPacket outputPacket = new DatagramPacket(data2,data2.length,senderAddress,senderPort);
                s.datagramSocket.send(outputPacket);
                s.datagramSocket.close();
            }catch(Exception e){
                System.err.println("Erro: "+e);
            }
//teste conexao cli
            //recebe mensagens da command line
            while (true) {
                System.out.print("> ");
                String msg = scan.next();

                if (msg.equalsIgnoreCase("exit")) {
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