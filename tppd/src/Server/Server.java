package Server;

import Dados.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private InteracaoDatabase db;
    private String ipDB;
    private int tcp_port;
    private List<String> users;
    private List<Socket> openedSockets;
    private Map<String, Streams> mapSockets;
    private static final int MAX_SIZE = 10000;

    public List<Socket> getOpenedSockets() {
        return this.openedSockets;
    }

    public Map<String, Streams> getMapSockets() {
        return this.mapSockets;
    }

    public Server(String ipDB, int id) throws RemoteException {
        this.tcp_port = id;
        this.ipDB = ipDB;
        setup();
    }

    public void setup() {
        try {
            this.db = new InteracaoDatabase(this.ipDB, this.tcp_port);

            this.users = new ArrayList<>();
            this.openedSockets = new ArrayList<>();
            this.mapSockets = new HashMap<>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean registaUtilizador(Utilizador utilizador) {

        if(this.db.isRegistered(utilizador.getUsername())){
            //setChanged();
            //notifyObservers(false);
            return false;
        }
        this.db.register(utilizador);

        this.users.add(utilizador.getUsername());
        return true;
    }



    public Utilizador ExecutaLogin(Login login, String ip){
        if(db.isConnected(login.getUsername())){
            return null;
        }
        Utilizador utilizador = db.login(login.getUsername(),login.getPassword(),ip);
        int found=0;
        if(utilizador!=null){
            System.out.println("Utilizador ligado");
            for(String user: users){
                if(user.equals(login.getUsername())){
                    found=1;
                }
            }
            if(found==0) {
                users.add(login.getUsername());
                return utilizador;
            }
        }
        return null;
    }

    public Boolean ForwardMensagem(Msg msg){
        Socket source,dest;
        if(users.contains(msg.getRecebe())){ // verifica se está no servidor
            dest = mapSockets.get(msg.getRecebe()).getSocket();
            try {
                ObjectOutputStream oout = mapSockets.get(msg.getRecebe()).getOout();
                oout.writeObject(msg);
                oout.flush();
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            if(db.isConnected(msg.getRecebe())){
                try {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bout);
                    DatagramSocket ds = new DatagramSocket(db.selectPortoUdp(msg.getRecebe()));
                    String mensagem = msg.getEnvia() + msg.getTexto();
                    out.writeObject(mensagem);
                    out.flush();
                    DatagramPacket packet = new DatagramPacket(bout.toByteArray(), bout.size(),db.selectIp(msg.getRecebe()),db.selectPortoUdp(msg.getRecebe()));
                    ds.send(packet);

                    packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    ds.receive(packet);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return true;
            }
            else{
                try { // avisa quem quer enviar a msg q o utilizador destino n esta ligado
                    /*ObjectOutputStream oout = new ObjectOutputStream(source.getOutputStream());
                    oout.writeObject("Utilizador destino n esstá ligado");
                    oout.flush();*/
                    return false;
                    //only return false
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public Database getDatabase(){
        Database database = new Database();
        database.setUsers(this.db.getConnectedUsers());
        Map<String,List<Ficheiro>> ficheiros = new HashMap<>();
        Map<String,List<Canal>> uploads = new HashMap<>();
        Map<String,List<Msg>> downloads = new HashMap<>();
        /*for(String user : database.getUsers()){
            ficheiros.put(user,getUserFiles(user));
            uploads.put(user,getUserUploads(user));
            downloads.put(user,getUserDownloads(user));
        }
        database.setDownloads(downloads);
        database.setFicheiros(ficheiros);
        database.setUploads(uploads);*/
        return database;
    }

    public void shutdown() throws SQLException {
        this.db.termina();
        System.out.println("depois do termina");
        this.db.shutdown();
    }
}