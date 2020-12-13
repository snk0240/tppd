package Server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;

public class ServerComm extends Thread {
    private static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

    private MulticastHandler multicastHandler;
    private TCPClientHandler tcpClientHandler;
    private UDPClientHandler udp_handler;

    private final ServerObservable serverObs;
    private final Server servidor;

    private MulticastSocket multicastSocket;
    private DatagramSocket datagramSocket;
    private ServerSocket server;
    private Socket nextClient;

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

    public void shutdown() throws IOException, InterruptedException, SQLException {
        this.isAlive = false;

        MsgMulticast msgMulticast = new MsgMulticast(this.SERVER_SHUTDOWN, this.portTCP);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msgMulticast);
        byte[] data1 = baos.toByteArray();
        DatagramPacket inputPacket = new DatagramPacket(data1, data1.length, InetAddress.getByName("230.0.0.0"), this.portMulticast);

        inputPacket.setData(data1);
        //informa via multicast que vai terminar
        this.multicastSocket.send(inputPacket);

        //fecha a coneccao BD
        this.servidor.shutdown();

        this.udp_handler.interrupt();
        this.udp_handler.join(1);
        this.tcpClientHandler.interrupt();
        this.tcpClientHandler.join(1);

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
