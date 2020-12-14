package Server;

import java.io.*;
import java.net.*;

public class MulticastKeepAlive extends Thread {
    private static int MAX_SIZE = 1000;

    private MulticastSocket multicastSocket = null;
    private ByteArrayOutputStream buff;
    private ObjectOutputStream out;
    private DatagramPacket packet;

    boolean isAlive;
    private int portTCP;

    public MulticastKeepAlive(MulticastSocket multicastSocket, int portTCP) {
        this.multicastSocket = multicastSocket;
        this.portTCP = portTCP;
        this.isAlive = true;
    }

    @Override
    public void run() {

        while (isAlive) {
            try {
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE, InetAddress.getByName("230.0.0.0"), 5432);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            buff = new ByteArrayOutputStream();
            try {
                out = new ObjectOutputStream(buff);
                out.writeObject(this.portTCP);

                packet.setData(buff.toByteArray()); //Preencher com um write object

                multicastSocket.send(packet);

                Thread.sleep(500);
            } catch (IOException e) {
                System.out.println("Erro nos objectos");
            } catch (InterruptedException e) {
                System.out.println("Erro no timeout");
            }
        }
    }
}
