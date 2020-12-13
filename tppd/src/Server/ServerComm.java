package Server;

import java.io.*;
import java.net.*;

public class ServerComm extends Thread {
    public static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

    private MulticastHandler multicastHandler;
    private TCPClientHandler tcpClientHandler;
    private UDPClientHandler udp_handler;

    final int MAX_CONNECTIONS = 10;
    private final ServerObservable serverObs;
    MulticastSocket multicastSocket = null;
    DatagramSocket datagramSocket = null;
    ServerSocket server = null;
    Socket nextClient = null;
    private boolean isAlive;

    int portUDP;
    int portTCP;
    String ipDB;
    final int portMulticast = 5432;
    final int portBDdefault = 3306;
    int portDB;
    int identificador;

    //InteracaoDatabase idb;
    InetAddress group;

    int nrServersOnline = 0;

    private Server servidor;

    public ServerComm(int UDP_port, int TCP_port, String DB_ip, Server servidor) throws UnknownHostException {
        serverObs = new ServerObservable();
        this.portUDP = UDP_port;
        this.portTCP = TCP_port;
        this.ipDB = DB_ip;
        this.group = InetAddress.getByName(DB_ip);
        this.isAlive = true;

        //acrescenta porto ao IP da BD
        //ir incrementando o portDB conforme o número de servers online
        this.portDB = this.portBDdefault + this.nrServersOnline;
        this.ipDB += ":" + this.portDB;

        this.servidor=servidor;
    }

    @Override
    public void run() {
        try {
            multicastSocket = new MulticastSocket(this.portMulticast);
            multicastSocket.joinGroup(InetAddress.getByName("230.0.0.0"));
            multicastHandler = new MulticastHandler(multicastSocket);
            multicastHandler.start();

            this.server = new ServerSocket(this.portTCP, MAX_CONNECTIONS);
            System.out.println("Server started");
        } catch (IOException e) {
            e.printStackTrace();
        }

        udp_handler = new UDPClientHandler();
        udp_handler.start();

        while (isAlive) {
            try {
                nextClient = server.accept();

                tcpClientHandler = new TCPClientHandler(nextClient, serverObs, portTCP, servidor);
                tcpClientHandler.start();
            } catch (BindException e) {
                System.err.println("Service already running on port " + portTCP);
            } catch (IOException e) {
                System.err.println("I/O error --" + e);
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
        multicastSocket.send(inputPacket);

        //avisar os clientes para terminarem
        //avisar os servidores que vai encerrar

        this.tcpClientHandler.join();

        if (this.nextClient != null)
            this.nextClient.close();

        if (this.server != null)
            this.server.close();

        //verifica se é o ultimo servidor online, se for encerra multicastsocket
        if (multicastSocket != null)
            multicastSocket.close();

        System.exit(0);
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
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
                    System.out.println("Esperando 1 conexao");
                    datagramSocket.receive(inputPacket);

                    String receivedData = new String(inputPacket.getData());
                    System.out.println("Recebi: " + receivedData);

                    InetAddress senderAddress = inputPacket.getAddress();
                    int senderPort = inputPacket.getPort();

                    byte[] data2 = Integer.toString(portTCP).getBytes();
                    DatagramPacket outputPacket = new DatagramPacket(data2, data2.length, senderAddress, senderPort);
                    datagramSocket.send(outputPacket);
                    System.out.println("Enviei o meu porto TCP!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
