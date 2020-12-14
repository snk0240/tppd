package Server;

import Dados.Msg;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

public class ServerComm extends Thread {
    private static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

    private MulticastHandler multicastHandler;
    private TCPClientHandler tcpClientHandler;
    private UDPClientHandler udp_handler;

    private ArrayList<Socket> l1;

    private final Server servidor;

    private MulticastSocket multicastSocket;
    private DatagramSocket datagramSocket;
    private ServerSocket serverSocket;
    private Socket nextClient;

    private final int MAX_CONNECTIONS = 10;
    private final String groupIP = "230.0.0.0";
    private boolean isAlive;

    private int portUDP;
    private int portTCP;
    private String ipDB;
    private final int portMulticast = 5432;

    public ServerComm(int UDP_port, int TCP_port, String DB_ip, Server servidor) throws UnknownHostException {
        this.portUDP = UDP_port;
        this.portTCP = TCP_port;
        this.ipDB = DB_ip;
        this.isAlive = true;
        this.l1 = new ArrayList<>();

        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            this.multicastSocket = new MulticastSocket(this.portMulticast);
            this.multicastSocket.joinGroup(InetAddress.getByName(this.groupIP));
            this.multicastHandler = new MulticastHandler(this.multicastSocket, this.portTCP, this.serverSocket);
            this.multicastHandler.start();

            this.serverSocket = new ServerSocket(this.portTCP, this.MAX_CONNECTIONS);
            //System.out.println("Server started");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.udp_handler = new UDPClientHandler();
        this.udp_handler.start();

        while (this.isAlive) {
            try {
                this.nextClient = this.serverSocket.accept();
                this.l1.add(this.nextClient);

                this.tcpClientHandler = new TCPClientHandler(this.nextClient, this.portTCP, this.servidor, this.multicastSocket);
                this.tcpClientHandler.start();
            } catch (BindException e) {
                System.err.println("Service already running on port " + this.portTCP);
            } catch (IOException e) {
                System.err.println("I/O error:" + e);
            }
        }

        try {
            this.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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

        //ao fechar o datagramsocket vai criar SocketException no entanto é um procedimento necessário para terminar a thread
        this.datagramSocket.close();
        this.udp_handler.join();

        if(this.tcpClientHandler != null) {
            this.tcpClientHandler.shutdown();
            this.tcpClientHandler.join();
        }

        this.multicastHandler.shutdown();

        if (this.nextClient != null)
            this.nextClient.close();

        if (this.serverSocket != null)
            this.serverSocket.close();

        if (this.multicastSocket != null)
            //ao fechar o multicastsocket vai criar SocketException no entanto é um procedimento necessário para terminar a thread
            this.multicastSocket.close();
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
                this.interrupt();
            }
        }
    }

    public class MulticastHandler extends Thread {
        private static final String NEW_SERVER = "NEW SERVER";
        private static final String NEW_USER = "NEW USER";
        private static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

        private MulticastSocket multicastSocket;
        private DatagramPacket receivingPacket;
        private ObjectInputStream in;
        private ByteArrayOutputStream buff;
        private ObjectOutputStream out;
        private Object request;
        private MsgMulticast msgMulticast;
        private ServerSocket serverSocket;

        private int portTCP;
        private boolean isAlive;
        private ArrayList<Integer> oldList;
        private ArrayList<Integer> newList;

        private MulticastKeepAlive multicastKeepAlive;
        private VerifyAliveList verifyAliveList;

        public MulticastHandler(MulticastSocket multicastSocket, int portTCP, ServerSocket serverSocket) {
            this.multicastSocket = multicastSocket;
            this.serverSocket = serverSocket;
            this.portTCP = portTCP;
            this.isAlive = true;
            this.newList = new ArrayList<>();
            this.oldList = new ArrayList<>();
            this.newList.add(portTCP);
            this.oldList.add(portTCP);

            this.multicastKeepAlive = new MulticastKeepAlive(this.multicastSocket, portTCP);
            this.multicastKeepAlive.start();

            this.verifyAliveList = new VerifyAliveList();
            this.verifyAliveList.start();
        }

        @Override
        public void run() {
            byte[] data2 = new byte[1024];
            this.receivingPacket = new DatagramPacket(data2, data2.length);

            while (this.isAlive) {
                try {
                    this.multicastSocket.receive(this.receivingPacket);

                    this.in = new ObjectInputStream(new ByteArrayInputStream(this.receivingPacket.getData()));

                    this.request = (this.in.readObject());

                    if (this.request instanceof Integer) {
                        boolean aux = false;
                        if (this.newList.contains(this.request)) {
                            aux = true;
                        }
                        if (!aux) {
                            String s = this.request.toString();
                            this.newList.add(Integer.parseInt(s));
                        }
                    }

                    if(this.request instanceof Msg) {
                        Msg m = (Msg) this.request;
                        System.out.println("Apanhei uma mensagem para dissiminar");

                        for(int i = 0; i < l1.size(); i++) {
                            this.out = new ObjectOutputStream(l1.get(i).getOutputStream());
                            this.out.writeObject(m.getTexto());
                            this.out.flush();
                        }
                    }

                    if(this.request instanceof MsgMulticast) {
                        this.msgMulticast = (MsgMulticast) this.request;

                        if (this.msgMulticast.tipoMsg.toUpperCase().contains(this.NEW_USER)) {
                            //rebece informacao de novo user

                        /*this.buff = new ByteArrayOutputStream();
                        out = new ObjectOutputStream(buff);
                        out.writeObject(this.portTCP);

                        receivingPacket.setData(this.buff.toByteArray()); //Preencher com um write object

                        multicastSocket.send(receivingPacket);*/
                        }
                        else if (this.msgMulticast.tipoMsg.toUpperCase().contains(this.SERVER_SHUTDOWN)) {
                            if(this.msgMulticast.port != this.portTCP) {
                                this.oldList.remove(this.msgMulticast.port);
                            }
                        }
                        else if (this.msgMulticast.tipoMsg.toUpperCase().contains(this.NEW_SERVER)) {
                            //recebe informação de um novo server
                        }
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public void shutdown() throws IOException, InterruptedException {
            this.isAlive = false;
            this.multicastKeepAlive.setAlive(false);

            if(this.multicastSocket != null)
                //ao fechar o multicastsocket vai criar SocketException no entanto é um procedimento necessário para terminar a thread
                this.multicastSocket.close();

            if(this.buff != null)
                this.buff.close();

            if(this.in != null)
                this.in.close();

            if(this.out != null)
                this.out.close();

            this.verifyAliveList.join();
            this.multicastKeepAlive.join();
        }

        public class VerifyAliveList extends Thread {

            public VerifyAliveList() {
            }

            @Override
            public void run() {

                while (isAlive) {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (newList != oldList) {
                        for (int i = 0; i < newList.size(); i++) {
                            if(oldList.contains(newList.get(i))) {
                                continue;
                            }
                            else {
                                //System.out.println("New server joined with port TCP: " + newList.get(i));
                                oldList.add(newList.get(i));
                                //do something with this information
                            }
                        }

                        for(int i = 0; i < oldList.size(); i++) {
                            if(newList.contains(oldList.get(i))) {
                                continue;
                            }
                            else {
                                //System.out.println("Server with port TCP: " + oldList.get(i) + " terminated");
                                //do something with this information
                            }
                        }

                        oldList = newList;
                        newList = new ArrayList<>();
                    }
                }
            }
        }
    }
}
