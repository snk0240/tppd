package Server;

import java.io.*;
import java.net.*;

public class MulticastKeepAlive extends Thread {
    private static int MAX_SIZE = 1000;

    private MulticastSocket multicastSocket = null;
    private ByteArrayOutputStream buff;
    private ObjectOutputStream out;
    private DatagramPacket packet;

    private boolean isAlive;
    private int portTCP;

    public MulticastKeepAlive(MulticastSocket multicastSocket, int portTCP) {
        this.multicastSocket = multicastSocket;
        this.portTCP = portTCP;
        this.isAlive = true;
    }

    @Override
    public void run() {

        while (this.isAlive) {
            try {
                this.packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE, InetAddress.getByName("230.0.0.0"), 5432);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            this.buff = new ByteArrayOutputStream();
            try {
                this.out = new ObjectOutputStream(this.buff);
                this.out.writeObject(this.portTCP);

                this.packet.setData(this.buff.toByteArray()); //Preencher com um write object

                this.multicastSocket.send(this.packet);

                Thread.sleep(500);
            } catch (IOException e) {
                System.out.println("Erro nos objectos");
            } catch (InterruptedException e) {
                System.out.println("Erro no timeout");
            }
        }
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
