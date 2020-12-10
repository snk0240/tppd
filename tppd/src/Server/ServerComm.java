package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;

public class ServerComm extends Thread{
    final int MAX_CONNECTIONS = 10;
    //private final ServerObservable serverObs;
    MulticastSocket multicastSocket = null;
    DatagramSocket datagramSocket = null;
    ServerSocket server = null;
    Socket nextClient = null;
    boolean isAlive;
    String response, receivemsg;

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
                //multicastSocket.joinGroup(this.group);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            datagramSocket = new DatagramSocket(portUDP);
            byte[] data1 = new byte[1024];

            DatagramPacket inputPacket = new DatagramPacket(data1, data1.length);
            System.out.println("Esperando 1 conexao");
            datagramSocket.receive(inputPacket);

            InetAddress senderAddress = inputPacket.getAddress();
            int senderPort = inputPacket.getPort();

            byte[] data2 = Integer.toString(portTCP).getBytes();
            DatagramPacket outputPacket = new DatagramPacket(data2,data2.length,senderAddress,senderPort);
            datagramSocket.send(outputPacket);
            System.out.println("Enviei o meu porto TCP!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            server = new ServerSocket(portTCP);
            System.out.println("Server started");
            while(true) {
                nextClient = server.accept();

                BufferedReader bin = new BufferedReader(new InputStreamReader(nextClient.getInputStream()));
                PrintStream pout = new PrintStream(nextClient.getOutputStream(), true);

                receivemsg = bin.readLine();
                System.out.println("Received request from " + nextClient.getInetAddress() + ":" + nextClient.getPort());

                response = "FDS DEMOROU MAS FOI";
                pout.println(response);

                nextClient.close();
            }
        } catch (BindException e) {
            System.err.println("Service already running on port " + portTCP);
        } catch (IOException e) {
            System.err.println("I/O error --" + e);
        } finally {
            if(server != null) {
                try { server.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }

        while (isAlive) {
            try {
                this.nextClient = this.server.accept();
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
}
