package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class ClienteComm extends Thread {

    InetAddress ip_server;
    int udp_port_server;
    int tcp_port_server;
    Socket socket;
    DatagramSocket datagramSocket;
    BufferedReader bin;
    PrintWriter pout;
    String response;

    byte[] data1 = new byte[1024];
    byte[] data2 = new byte[1024];

    public ClienteComm(InetAddress ip, int udp){
        ip_server = ip;
        udp_port_server = udp;
    }

    @Override
    public synchronized void start() {
        try {
            datagramSocket = new DatagramSocket();

            DatagramPacket sendingPacket = new DatagramPacket(data1, data1.length, ip_server, udp_port_server);
            datagramSocket.send(sendingPacket);

            DatagramPacket receivingPacket = new DatagramPacket(data2, data2.length);
            datagramSocket.receive(receivingPacket);

            String receivedData = new String(receivingPacket.getData());
            System.out.println("Recebi: " + receivedData);

            tcp_port_server = Integer.parseInt(receivedData.trim());
            datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket = new Socket(ip_server, tcp_port_server);
            System.out.println("Connection established");
            // SET THE SOCKET OPTION JUST IN CASE SERVER STALLS
            //socket.setSoTimeout(10*1000); //ms
            pout = new PrintWriter(socket.getOutputStream(), true);//PrintWriter(new FileOutputStream("ola.txt")); escrevia para um ficheiro//true -> autoflush
            bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pout.println("dar o toque ao server");
            //pout.flush();
            response = bin.readLine();

            System.out.println("Recebido : " + response);

        } catch (IOException e) { //catches also InterruptedIOException
            System.err.println("Error " + e);
        }
    }

    public void shutdown() {
        try {
            socket.close();
            datagramSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
