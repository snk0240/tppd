package Server;

import java.io.*;
import java.net.*;

public class ServerComm extends Thread{
    final int MAX_CONNECTIONS = 10;
    //private final ServerObservable serverObs;
    MulticastSocket multicastSocket = null;
    DatagramSocket datagramSocket = null;
    ServerSocket server = null;
    Socket nextClient = null;
    boolean isAlive;

    int portUDP;
    int portTCP;
    String ipDB;
    final int portMulticast = 5432;
    final int portBDdefault = 3306;
    int portDB;

    InetAddress group;

    int nrServersOnline = 0;

    public ServerComm(int UDP_port, int TCP_port, String DB_ip) throws UnknownHostException {
        this.portUDP = UDP_port;
        this.portTCP = TCP_port;
        this.ipDB = DB_ip;
        this.group = InetAddress.getByName(DB_ip);
        //serverObs = new ServerObservable();
        isAlive = true;

        //acrescenta porto ao IP da BD
        //ir incrementando o portDB conforme o número de servers online
        this.portDB = this.portBDdefault + this.nrServersOnline;
        this.ipDB += ":" + this.portDB;

        //server cria a sua bd e conecta-se à mesma
        InteracaoDatabase idb = new InteracaoDatabase(ipDB);
    }

    @Override
    public void run() {
        //verifica se é o primeiro servidor a iniciar, caso seja cria o multicast, caso não seja conecta-se ao existente
        if(multicastSocket == null) {
            try {
                multicastSocket = new MulticastSocket(this.portMulticast);
                multicastSocket.joinGroup(InetAddress.getByName("230.0.0.0"));
                String texto = "Hello from udp multicast!";
                byte[] data1 = texto.getBytes();
                DatagramPacket inputPacket = new DatagramPacket(data1, data1.length, InetAddress.getByName("230.0.0.0"), this.portMulticast);
                //ByteArrayOutputStream buff = new ByteArrayOutputStream();
                //ObjectOutputStream out = new ObjectOutputStream(buff);
                //out.writeBytes(texto);

                inputPacket.setData(data1);
                multicastSocket.send(inputPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.server = new ServerSocket(this.portTCP, MAX_CONNECTIONS);
            datagramSocket = new DatagramSocket(portUDP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isAlive) {
            try {
                //this.nextClient = this.server.accept();
                byte[] data2 = new byte[1024];
                DatagramPacket receivingPacket = new DatagramPacket(data2,data2.length, InetAddress.getByName("230.0.0.0"), this.portMulticast);
                multicastSocket.receive(receivingPacket);
                //ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(receivingPacket.getData()));
                String receivedData = new String(receivingPacket.getData());
                System.out.println(receivedData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.nextClient.close();
            this.server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException {
        this.isAlive = false;

        //avisar os clientes para terminarem
        //avisar os servidores que vai encerrar

        if(this.nextClient != null)
            this.nextClient.close();

        if(this.server != null)
            this.server.close();

        //verifica se é o ultimo servidor online, se for encerra multicastsocket
        if(multicastSocket != null)
            multicastSocket.close();

        System.exit(0);
    }

    public DatagramSocket getDatagramSocket(){
        return datagramSocket;
    }
}
