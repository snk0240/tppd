package Client;

import java.io.*;
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

    TransferenciaFicheiros transferenciaFicheiros;
    int identificador;

    byte[] data1 = new byte[1024];
    byte[] data2 = new byte[1024];

    public ClienteComm(InetAddress ip, int udp, TransferenciaFicheiros transferencia){
        ip_server = ip;
        udp_port_server = udp;
        transferenciaFicheiros = transferencia;
        identificador++;
    }

    @Override
    public synchronized void start() {
        try {
            datagramSocket = new DatagramSocket();

            byte[] data1 = Integer.toString(identificador).getBytes();
            DatagramPacket sendingPacket = new DatagramPacket(data1, data1.length, ip_server, udp_port_server);
            datagramSocket.send(sendingPacket);

            DatagramPacket receivingPacket = new DatagramPacket(data2, data2.length);
            datagramSocket.receive(receivingPacket);

            String receivedData = new String(receivingPacket.getData());
            System.out.println("Recebi: " + receivedData.trim());

            tcp_port_server = Integer.parseInt(receivedData.trim());
            datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Socket socket = new Socket(ip_server, tcp_port_server)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println("CUCU DO CLIENT");

            InputStream input = socket.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
