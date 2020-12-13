package Client;

import Dados.Login;
import Dados.Utilizador;

import java.io.*;
import java.net.*;

public class ClienteComm extends Thread {

    InetAddress ip_server;
    int udp_port_server;
    int tcp_port_server;
    Socket socket;
    DatagramSocket datagramSocket;

    ObjectOutputStream oout;
    ObjectInputStream oin;

    TransferenciaFicheiros transferenciaFicheiros;
    int identificador;
    boolean autenticado=false;

    byte[] data1 = new byte[1024];
    byte[] data2 = new byte[1024];

    public ClienteComm(InetAddress ip, int udp, TransferenciaFicheiros transferencia){
        ip_server = ip;
        udp_port_server = udp;
        transferenciaFicheiros = transferencia;
        identificador = 1;
    }

    @Override
    public void run() {
        try {
            datagramSocket = new DatagramSocket();

            byte[] data1 = Integer.toString(identificador).getBytes();
            DatagramPacket sendingPacket = new DatagramPacket(data1, data1.length, ip_server, udp_port_server);
            datagramSocket.send(sendingPacket);

            DatagramPacket receivingPacket = new DatagramPacket(data2, data2.length);
            datagramSocket.receive(receivingPacket);

            String receivedData = new String(receivingPacket.getData());
            System.out.println("Recived TCP Port Server by UDP and it is " + receivedData.trim());

            tcp_port_server = Integer.parseInt(receivedData.trim());
            datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Socket socket = new Socket(ip_server, tcp_port_server);
            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());
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
            oout.writeObject(login);
            oout.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void registo(Utilizador utilizador){
        Boolean autenticado = false;
        try {
            //oout.reset();
            oout.writeObject(utilizador);
            oout.flush();
            //ois = new ObjectInputStream(socketServidor.getInputStream());
            //autenticado = ois.readBoolean();
        } catch (IOException | NullPointerException  e) {
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

    public void getDatabase(){
        try{
            oout.writeObject("getDatabase");
            oout.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
