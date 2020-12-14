package Server;

import Dados.Login;
import Dados.Msg;
import Dados.Streams;
import Dados.Utilizador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class TCPClientHandler extends Thread {
    private final Socket s;

    private boolean isAlive;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private ObjectOutputStream notificationOut;
    private Socket notification;

    private Server servidor;
    private int tcp_port;
    private Streams streams;

    public TCPClientHandler(Socket s, int tcp_port, Server server) {
        this.s = s;
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
        Boolean registo, mensagem;
        String user;
        Utilizador login;

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

                else if(received instanceof Login){
                    String ip=s.getInetAddress().getHostAddress();
                    login=servidor.ExecutaLogin((Login)received,ip);
                    System.out.println(login);

                    out.writeObject((Utilizador)login);
                    out.flush();
                    if(login!=null){
                        user = ((Utilizador) login).getUsername();
                        servidor.getMapSockets().put(user,streams);
                    }
                }

                else if(received instanceof String){
                    if(received.equals("getDatabase")){
                        out.writeObject(servidor.getDatabase());
                        out.flush();
                    }
                }

                else if(received instanceof Msg){
                    Msg msg = (Msg)received;
                    System.out.println("recebi msg: "+msg.getTexto()+" "+msg.getEnvia()+" "+msg.getRecebe());
                    /*if(msg.getRecebe()==null){
                        server.ClientDisconnected(msg.getEnvia());
                    }
                    else {*/
                        System.out.println("A encaminhar msg");
                        //dbServer.BroadcastMensagem(msg);
                        mensagem = servidor.ForwardMensagem(msg);
                        out.writeObject(mensagem);
                        out.flush();
                    //}
                }
                else {
                }
            }catch(Exception e){
                System.out.println(e);
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
                this.in.close();
                this.out.close();
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
}
