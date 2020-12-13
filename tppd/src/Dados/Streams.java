package Dados;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Streams {
    private ObjectOutputStream oout;
    private ObjectInputStream oin;
    private Socket socket;

    public Streams(){ }

    public ObjectOutputStream getOout() {
        return oout;
    }

    public void setOout(ObjectOutputStream oout) {
        this.oout = oout;
    }

    public ObjectInputStream getOin() {
        return oin;
    }

    public void setOin(ObjectInputStream oin) {
        this.oin = oin;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
