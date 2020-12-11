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
    public static final String NEW_SERVER = "NEW SERVER";
    public static final String NEW_USER = "NEW USER";
    public static final String SERVER_SHUTDOWN = "SERVER SHUTDOWN";

    MulticastSocket multicastSocket = null;
    ObjectInputStream in;
    ObjectOutputStream out;
    Object request;
    MsgMulticast msgMulticast;

    public MulticastHandler(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        byte[] data2 = new byte[1024];
        DatagramPacket receivingPacket = new DatagramPacket(data2, data2.length);
        try {
            this.multicastSocket.receive(receivingPacket);

            in = new ObjectInputStream(new ByteArrayInputStream(receivingPacket.getData()));

            request = (in.readObject());

            System.out.println();
            System.out.print("(" + receivingPacket.getAddress().getHostAddress() + ":" + receivingPacket.getPort() + ") ");

            msgMulticast = (MsgMulticast) request;

            if (msgMulticast.getTipoMsg().toUpperCase().contains(NEW_USER)) {
                        /*ByteArrayOutputStream buff = new ByteArrayOutputStream();
                        out = new ObjectOutputStream(buff);
                        out.writeObject(ipDB);

                        receivingPacket.setData(buff.toByteArray()); //Preencher com um write object

                        multicastSocket.send(receivingPacket);*/
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

            //Mostra a mensagem recebida bem como a identificacao do emissor
            System.out.println("Recebido \"" + msgMulticast.getMsg() + "\" de ");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

/* try {

                    // "Deserializa" o objecto transportado no datagrama acabado de ser recebido
                    in = new ObjectInputStream(new ByteArrayInputStream(receivingPacket.getData()));

                    request = (in.readObject());

                    System.out.println();
                    System.out.print("(" + receivingPacket.getAddress().getHostAddress() + ":" + receivingPacket.getPort() + ") ");

                    msgMulticast = (MsgMulticast) request;

                    if (msgMulticast.getTipoMsg().toUpperCase().contains(NEW_USER)) {

                        ByteArrayOutputStream buff = new ByteArrayOutputStream();
                        out = new ObjectOutputStream(buff);
                        out.writeObject(ipDB);

                        receivingPacket.setData(buff.toByteArray()); //Preencher com um write object

                        multicastSocket.send(receivingPacket);
                        continue;
                                }
                                else if (msgMulticast.getTipoMsg().toUpperCase().contains(NEW_SERVER)) {

                                //recebe informação de um novo server
                                continue;
                                }
                                else if (msgMulticast.getTipoMsg().toUpperCase().contains(SERVER_SHUTDOWN)) {
                                continue;
                                }
                                else if (msgMulticast.getTipoMsg().toUpperCase().contains("TESTE")) {
                                if (this.portUDP == 5002)
                                shutdown();
                                continue;
                                }

                                //Mostra a mensagem recebida bem como a identificacao do emissor
                                System.out.println("Recebido \"" + msgMulticast.getMsg() + "\" de " + ipDB);

                                } catch (ClassNotFoundException e) {

                                System.out.println();
                                System.out.println("Mensagem recebida de tipo inesperado!");
                                //continue;

                                } catch (IOException e) {

                                System.out.println();
                                System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida!");
                                //continue;

                                }*/