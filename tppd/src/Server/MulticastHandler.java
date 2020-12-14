package Server;

import Dados.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
                    if (newList.contains(this.request)) {
                        aux = true;
                    }
                    if (!aux) {
                        String s = this.request.toString();
                        newList.add(Integer.parseInt(s));
                    }
                }

                if(this.request instanceof Msg) {
                    System.out.println("Apanhei uma mensagem para dissiminar");
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