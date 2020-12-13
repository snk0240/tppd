package Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

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

public class MulticastHandler extends Thread{
    private static final String NEW_SERVER = "NEW SERVER";
    private static final String NEW_USER = "NEW USER";
    private static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

    private MulticastSocket multicastSocket = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Object request;
    private MsgMulticast msgMulticast;

    private boolean isAlive;

    MulticastKeepAlive multicastKeepAlive;

    public MulticastHandler(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
        this.multicastKeepAlive = new MulticastKeepAlive(multicastSocket);
        this.multicastKeepAlive.start();

        this.isAlive = true;
    }

    @Override
    public void run() {
        byte[] data2 = new byte[1024];
        DatagramPacket receivingPacket = new DatagramPacket(data2, data2.length);

        while(this.isAlive) {
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
                if (this.request instanceof String) {
                    //DESCOMENTAR PING//System.out.println(request.toString());
                }

                //Mostra a mensagem recebida bem como a identificacao do emissor
                //System.out.println("Recebido \"" + msgMulticast.getMsg() + "\" de ");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}