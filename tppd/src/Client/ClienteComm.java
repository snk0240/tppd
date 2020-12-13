package Client;

import Dados.Login;
import Dados.Utilizador;

import java.io.*;
import java.net.*;

public class ClienteComm extends Thread {

    private InetAddress ip_server;
    private int udp_port_server;
    private int tcp_port_server;
    private Socket socket;
    private DatagramSocket datagramSocket;

    private ObjectInputStream oin;
    private ObjectOutputStream oout;

    private TransferenciaFicheiros transferenciaFicheiros;
    private boolean autenticado = false;

    byte[] data1 = new byte[1024];
    byte[] data2 = new byte[1024];

    public ClienteComm(InetAddress ip, int udp, TransferenciaFicheiros transferencia){
        this.ip_server = ip;
        this.udp_port_server = udp;
        this.transferenciaFicheiros = transferencia;
    }

    @Override
    public void run() {
        try {
            this.datagramSocket = new DatagramSocket();

            byte[] data1 = Integer.toString(1).getBytes();
            DatagramPacket sendingPacket = new DatagramPacket(data1, data1.length, ip_server, udp_port_server);
            this.datagramSocket.send(sendingPacket);

            DatagramPacket receivingPacket = new DatagramPacket(data2, data2.length);
            this.datagramSocket.receive(receivingPacket);

            String receivedData = new String(receivingPacket.getData());
            System.out.println("Recived TCP Port Server by UDP and it is " + receivedData.trim());

            this.tcp_port_server = Integer.parseInt(receivedData.trim());
            this.datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Socket socket = new Socket(this.ip_server, this.tcp_port_server);
            oin = new ObjectInputStream(socket.getInputStream());
            oout = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("TCP Socket created successfully!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login(Login login)
    {
        Object obj;
        try {
            //oout.reset();
            this.oout.writeObject(login);
            this.oout.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void registo(Utilizador utilizador){
        Boolean autenticado = false;
        try {
            //this.oout.reset();
            this.oout.writeObject(utilizador);
            this.oout.flush();
            this.autenticado = (Boolean)oin.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.socket.close();
            this.datagramSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public void getDatabase(){
        try{
            this.oout.writeObject("getDatabase");
            this.oout.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getUdp_port_server() {
        return udp_port_server;
    }

    public void setUdp_port_server(int udp_port_server) {
        this.udp_port_server = udp_port_server;
    }

    public int getTcp_port_server() {
        return tcp_port_server;
    }

    public void setTcp_port_server(int tcp_port_server) {
        this.tcp_port_server = tcp_port_server;
    }

    public TransferenciaFicheiros getTransferenciaFicheiros() {
        return transferenciaFicheiros;
    }

    public void setTransferenciaFicheiros(TransferenciaFicheiros transferenciaFicheiros) {
        this.transferenciaFicheiros = transferenciaFicheiros;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
}
