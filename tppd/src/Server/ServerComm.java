package Server;

import Dados.Utilizador;

import java.io.*;
import java.net.*;

public class ServerComm extends Thread {
    private static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

    private MulticastHandler multicastHandler;
    private TCPClientHandler tcpClientHandler;
    private UDPClientHandler udp_handler;

    private final ServerObservable serverObs;
    private final Server servidor;

    private MulticastSocket multicastSocket = null;
    private DatagramSocket datagramSocket = null;
    private ServerSocket server = null;
    private Socket nextClient = null;

    private final int MAX_CONNECTIONS = 10;
    private final String groupIP = "230.0.0.0";
    private boolean isAlive;

    private int portUDP;
    private int portTCP;
    private String ipDB;
    private final int portMulticast = 5432;

    public ServerComm(int UDP_port, int TCP_port, String DB_ip, Server servidor) throws UnknownHostException {
        this.serverObs = new ServerObservable();
        this.portUDP = UDP_port;
        this.portTCP = TCP_port;
        this.ipDB = DB_ip;
        this.isAlive = true;

        this.servidor=servidor;
    }

    @Override
    public void run() {
        try {
            this.multicastSocket = new MulticastSocket(this.portMulticast);
            this.multicastSocket.joinGroup(InetAddress.getByName(this.groupIP));
            this.multicastHandler = new MulticastHandler(this.multicastSocket, this.portTCP);
            this.multicastHandler.start();

            this.server = new ServerSocket(this.portTCP, this.MAX_CONNECTIONS);
            //System.out.println("Server started");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.udp_handler = new UDPClientHandler();
        this.udp_handler.start();

        while (this.isAlive) {
            try {
                this.nextClient = this.server.accept();

                this.tcpClientHandler = new TCPClientHandler(this.nextClient, this.serverObs, this.portTCP, this.servidor);
                this.tcpClientHandler.start();
            } catch (BindException e) {
                System.err.println("Service already running on port " + this.portTCP);
            } catch (IOException e) {
                System.err.println("I/O error:" + e);
            }
        }

        try {
            this.tcpClientHandler.join();
            this.nextClient.close();
            this.server.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException, InterruptedException {
        this.isAlive = false;

        MsgMulticast msgMulticast = new MsgMulticast(this.SERVER_SHUTDOWN, "Im out nigga");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msgMulticast);
        byte[] data1 = baos.toByteArray();
        DatagramPacket inputPacket = new DatagramPacket(data1, data1.length, InetAddress.getByName("230.0.0.0"), this.portMulticast);

        inputPacket.setData(data1);
        this.multicastSocket.send(inputPacket);

        //avisar os clientes para terminarem
        //avisar os servidores que vai encerrar

        //this.tcpClientHandler.join();

        if (this.nextClient != null)
            this.nextClient.close();

        if (this.server != null)
            this.server.close();

        if (this.multicastSocket != null)
            this.multicastSocket.close();

        System.exit(0);
    }

    public class UDPClientHandler extends Thread {
        UDPClientHandler() { }

        @Override
        public void run() {
            try {
                datagramSocket = new DatagramSocket(portUDP);
                while (isAlive) {
                    byte[] data1 = new byte[1024];
                    DatagramPacket inputPacket = new DatagramPacket(data1, data1.length);
                    //System.out.println("Waiting for connection ...");
                    datagramSocket.receive(inputPacket);

                    String receivedData = new String(inputPacket.getData());
                    //System.out.println("Recived: " + receivedData);

                    InetAddress senderAddress = inputPacket.getAddress();
                    int senderPort = inputPacket.getPort();

                    byte[] data2 = Integer.toString(portTCP).getBytes();
                    DatagramPacket outputPacket = new DatagramPacket(data2, data2.length, senderAddress, senderPort);
                    datagramSocket.send(outputPacket);
                    //System.out.println("I just send my TCP port!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
