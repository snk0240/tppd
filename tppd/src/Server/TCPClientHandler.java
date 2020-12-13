package Server;

import Dados.Streams;
import Dados.Utilizador;

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
        this.serverObsClient = serverObs;
        this.isAlive = true;
        this.servidor = server;
        this.tcp_port = tcp_port;
        try {
            this.out = new ObjectOutputStream(s.getOutputStream());
            this.in = new ObjectInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.streams = new Streams();
        this.streams.setOin(this.in);
        this.streams.setOout(this.out);
        this.streams.setSocket(this.s);
    }

    @Override
    public void run() {
        Object received;
        Boolean registo;
        String user;

        while (this.isAlive) {
            try {
                // receive the answer from client
                received = this.in.readObject();

                if(received instanceof Utilizador){
                    registo = this.servidor.registaUtilizador((Utilizador)received);
                    System.out.println(((Utilizador) received).toDB());
                    this.out.writeObject(registo);
                    this.out.flush();
                    if(registo == true){
                        user = ((Utilizador) received).getUsername();
                        this.servidor.getMapSockets().put(user, this.streams);
                    }
                }
                else if(received instanceof String){
                    if(received.equals("DEMOROU MAS DEU")){
                        System.out.println("DEMOROU MAS FOI");
                    }
                }
                else {
                }
            }catch(Exception e){
                System.out.println(e);
            }
            try{
                out.reset();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        this.isAlive = false;

        /*if(this.notification != null){
            try {
                synchronized(this.notification){
                    //notificationOut.writeObject(new ServerShutdown());
                    this.notificationOut.flush();
                }
            } catch (IOException e) {
                System.err.println("Couldn't send shutdown packet: " + e);
            }
        }*/

        try {
            if(this.s != null) {
                this.s.close();
            }
        } catch (IOException e) {
            System.err.println("Could not close the socket!");
        }

        /*try {
            if(this.notification != null)
                this.notification.close();
        } catch (IOException e) {
            System.err.println("Could not close the socket!");
        }*/
    }

    private synchronized void writeObject(Object obj) throws IOException {
        this.out.writeObject(obj);
        this.out.flush();
    }

    public void notify(Object arg){
        try{
            synchronized(this.notification){
                this.notificationOut.writeObject(arg);
                this.notificationOut.flush();
            }
        }catch(IOException e){
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try{
            synchronized(this.notification){
                this.notificationOut.writeObject(arg);
                this.notificationOut.flush();
            }
        }catch(IOException e){
        }
    }
}
