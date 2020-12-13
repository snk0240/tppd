package Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

class MsgMulticast implements Serializable {
    protected String tipoMsg;
    protected String msg;

    public MsgMulticast(String tipo, String msg) {
        this.tipoMsg = tipo;
        this.msg = msg;
    }

    public String getTipoMsg() {
        return tipoMsg;
    }

    public String getMsg() {
        return msg;
    }
}

public class MulticastHandler extends Thread {
    private static final String NEW_SERVER = "NEW SERVER";
    private static final String NEW_USER = "NEW USER";
    private static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

    private MulticastSocket multicastSocket = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Object request;
    private MsgMulticast msgMulticast;

    private boolean isAlive;
    private ArrayList<Integer> oldList;
    private ArrayList<Integer> newList;

    private MulticastKeepAlive multicastKeepAlive;
    private VerifyAliveList verifyAliveList;

    public MulticastHandler(MulticastSocket multicastSocket, int portTCP) {
        this.multicastSocket = multicastSocket;
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
        DatagramPacket receivingPacket = new DatagramPacket(data2, data2.length);

        while (this.isAlive) {
            try {
                this.multicastSocket.receive(receivingPacket);

                this.in = new ObjectInputStream(new ByteArrayInputStream(receivingPacket.getData()));

                this.request = (this.in.readObject());

                //DESCOMENTAR PING//System.out.println();
                //DESCOMENTAR PING//System.out.print("(" + receivingPacket.getAddress().getHostAddress() + ":" + receivingPacket.getPort() + ") ");

            /*msgMulticast = (MsgMulticast) request;

            if (msgMulticast.getTipoMsg().toUpperCase().contains(NEW_USER)) {
                        *//*ByteArrayOutputStream buff = new ByteArrayOutputStream();
                        out = new ObjectOutputStream(buff);
                        out.writeObject(ipDB);

                        receivingPacket.setData(buff.toByteArray()); //Preencher com um write object

                        multicastSocket.send(receivingPacket);*//*
            }
            else if (msgMulticast.getTipoMsg().toUpperCase().contains(NEW_SERVER)) {
                //recebe informação de um novo server
            }
            else if (msgMulticast.getTipoMsg().toUpperCase().contains(SERVER_SHUTDOWN)) {
                //shutdown();
            }
            else if (msgMulticast.getTipoMsg().toUpperCase().contains("TESTE")) {
                //shutdown();
            }
            else */
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

                //Mostra a mensagem recebida bem como a identificacao do emissor
                //System.out.println("Recebido \"" + msgMulticast.getMsg() + "\" de ");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
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