package Server;

import java.io.*;
import java.net.*;
import java.sql.*;

public class ServerComm extends Thread {
    public static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

    private MulticastHandler multicastHandler;

    final int MAX_CONNECTIONS = 10;
    private final ServerObservable serverObs;
    MulticastSocket multicastSocket = null;
    DatagramSocket datagramSocket = null;
    ServerSocket server = null;
    Socket nextClient = null;
    private boolean isAlive;
    String response, receivemsg;

    int portUDP;
    int portTCP;
    String ipDB;
    final int portMulticast = 5432;
    final int portBDdefault = 3306;
    int portDB;

    String sql1 = "CREATE TABLE teste1.canal (" +
            "id INT NOT NULL AUTO_INCREMENT," +
            "nome VARCHAR(15) DEFAULT NULL," +
            "descricao VARCHAR(200) DEFAULT NULL," +
            "username VARCHAR(15) DEFAULT NULL," +
            "password VARCHAR(15) DEFAULT NULL," +
            "PRIMARY KEY (id))";

    String sql2 = "CREATE TABLE teste1.ficheiro (" +
            "id INT NOT NULL AUTO_INCREMENT," +
            "username VARCHAR(15) DEFAULT NULL," +
            "caminho VARCHAR(200) DEFAULT NULL," +
            "tamanho BIGINT DEFAULT NULL," +
            "tipo INT DEFAULT NULL," +
            "PRIMARY KEY (id))";

    String sql3 = "CREATE TABLE teste1.msg (" +
            "id INT NOT NULL AUTO_INCREMENT," +
            "texto VARCHAR(1024) DEFAULT NULL," +
            "id_chanel INT DEFAULT NULL," +
            "id_ficheiro INT DEFAULT NULL," +
            "username VARCHAR(15) DEFAULT NULL," +
            "envia VARCHAR(20) DEFAULT NULL," +
            "recebe VARCHAR(20) DEFAULT NULL," +
            "PRIMARY KEY (id))";

    String sql4 = "CREATE TABLE teste1.server (" +
            "id int NOT NULL AUTO_INCREMENT," +
            "ip VARCHAR(20) DEFAULT NULL," +
            "udp_port INT DEFAULT NULL," +
            "tcp_port INT DEFAULT NULL," +
            "PRIMARY KEY (id))";

    String sql5 = "CREATE TABLE teste1.user (" +
            "id INT NOT NULL AUTO_INCREMENT," +
            "nome VARCHAR(30) DEFAULT NULL," +
            "username VARCHAR(15) DEFAULT NULL," +
            "password VARCHAR(15) DEFAULT NULL," +
            "ip VARCHAR(20) DEFAULT NULL," +
            "udp_port INT DEFAULT NULL," +
            "tcp_port INT DEFAULT NULL," +
            "ativo TINYINT DEFAULT '0'," +
            "imagem VARCHAR(150) DEFAULT NULL," +
            "PRIMARY KEY (id))";

    String sql6 = "INSERT INTO teste1.user VALUES " +
            "(1,'andre joao','andre123','andre123','127.0.0.1',3636,3636,0,NULL)," +
            "(2,'andre sousa','andre321','andre321','127.0.0.1',3737,3737,0,NULL);";

    InteracaoDatabase idb;

    InetAddress group;

    int nrServersOnline = 0;

    public ServerComm(int UDP_port, int TCP_port, String DB_ip) throws UnknownHostException {
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

        //server cria a sua bd e conecta-se à mesma
        idb = new InteracaoDatabase(ipDB);
    }

    @Override
    public void run() {
        try {
            multicastSocket = new MulticastSocket(this.portMulticast);
            multicastSocket.joinGroup(InetAddress.getByName("230.0.0.0"));
            multicastHandler = new MulticastHandler(multicastSocket);
            multicastHandler.run();

            this.server = new ServerSocket(this.portTCP, MAX_CONNECTIONS);
            System.out.println("Server started");
            datagramSocket = new DatagramSocket(portUDP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isAlive) {

            /*try {
                byte[] data1 = new byte[1024];
                DatagramPacket inputPacket = new DatagramPacket(data1, data1.length);
                System.out.println("Esperando 1 conexao");
                datagramSocket.receive(inputPacket);

                String receivedData = new String(inputPacket.getData());
                System.out.println("Enviado: " + receivedData);

                //Verificar qual o servidor para o qual o cliente vai ser redirecionado
                byte[] data2 = Integer.toString(portTCP).getBytes();

                InetAddress senderAddress = inputPacket.getAddress();
                int senderPort = inputPacket.getPort();

                DatagramPacket outputPacket = new DatagramPacket(data2, data2.length, senderAddress, senderPort);
                datagramSocket.send(outputPacket);
                datagramSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }*/
            try {
                // GET THE NEXT TCP CLIENT
                nextClient = server.accept();
                System.out.println("Received request from " + nextClient.getInetAddress() + ":" + nextClient.getPort());

                BufferedReader bin = new BufferedReader(new InputStreamReader(nextClient.getInputStream()));
                PrintStream pout = new PrintStream(nextClient.getOutputStream(), true);

                receivemsg = bin.readLine();
                System.out.println("Received request from " + nextClient.getInetAddress() + ":" + nextClient.getPort());

                response = "FDS DEMOROU MAS FOI";
                pout.println(response);

                nextClient.close();

                /*try {
                    System.out.println("Creating Database");
                    Connection conn = idb.getConnection();
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(sql1);
                    stmt.executeUpdate(sql2);
                    stmt.executeUpdate(sql3);
                    stmt.executeUpdate(sql4);
                    stmt.executeUpdate(sql5);
                    stmt.executeUpdate(sql6);
                    System.out.println("Database created successfully...");
                } catch (SQLException e) {
                    e.printStackTrace();
                }*/

                // create a new thread object
                TCPClientHandler t = new TCPClientHandler(nextClient, serverObs);
                // Invoking the start() method
                t.start();

            } catch (BindException e) {
                System.err.println("Service already running on port " + portTCP);
            } catch (IOException e) {
                System.err.println("I/O error --" + e);
            } finally {
                if (server != null) {
                    try {
                        server.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
}
