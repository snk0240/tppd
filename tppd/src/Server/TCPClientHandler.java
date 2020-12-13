package Server;

import Dados.Streams;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class TCPClientHandler extends Thread implements Observer {
    private final ServerObservable serverObsClient;
    private final Socket s;

    private boolean isAlive;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private ObjectOutputStream notificationOut;
    private Socket notification;

    private Server servidor;
    private int tcp_port;
    private Streams streams;

    public TCPClientHandler(Socket s, ServerObservable serverObs, int tcp_port, Server server) {
        this.s = s;
        serverObsClient = serverObs;
        this.isAlive = true;
        this.servidor = server;
        this.tcp_port = tcp_port;
        try {
            out = new ObjectOutputStream(s.getOutputStream());
            in = new ObjectInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        streams = new Streams();
        streams.setOin(in);
        streams.setOout(out);
        streams.setSocket(s);
    }

    @Override
    public void run() {
        Object received;

        while (isAlive) {
            try {
                // receive the answer from client
                received = in.readObject();

                if(received instanceof String){
                    if(received.equals("DEMOROU MAS DEU")){
                        System.out.println("DEMOROU MAS FOI");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        this.exit();
    }

    public void exit() {
        isAlive = false;

        if(notification != null){
            try {
                synchronized(notification){
                    //notificationOut.writeObject(new ServerShutdown());
                    notificationOut.flush();
                }
            } catch (IOException e) {
                System.err.println("Couldn't send shutdown packet: " + e);
            }
        }

        try {
            s.close();
        } catch (IOException e) {
            System.err.println("Could not close the socket!");
        }

        try {
            if(notification != null)
                notification.close();
        } catch (IOException e) {
            System.err.println("Could not close the socket!");
        }
    }

    private synchronized void writeObject(Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
    }

    public void notify(Object arg){
        try{
            synchronized(notification){
                notificationOut.writeObject(arg);
                notificationOut.flush();
            }
        }catch(IOException e){
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try{
            synchronized(notification){
                notificationOut.writeObject(arg);
                notificationOut.flush();
            }
        }catch(IOException e){
        }
    }
}
