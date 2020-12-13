package Server;

import Dados.*;

import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
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
}