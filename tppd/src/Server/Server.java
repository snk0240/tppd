package Server;

import Client.ClientMain;
import Dados.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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

    public Utilizador registaUtilizador(Utilizador utilizador) {

        if(this.db.isRegistered(utilizador.getUsername())){
            return null;
        }
        this.db.register(utilizador);

        this.users.add(utilizador.getUsername());
        return utilizador;
    }

    public Utilizador ExecutaLogin(Login login, String ip) throws RemoteException{
        if(db.isConnected(login.getUsername())){
            return null;
        }
        Utilizador utilizador = db.login(login.getUsername(),login.getPassword(),ip);
        int found=0;
        if(utilizador!=null){
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
    public Boolean ForwardMensagem2(Msgt msg) throws RemoteException{
        //codigo para enviar a mensagem recebida para todos os utilizadores ligados a este servidor
        return true;
    }


    public Boolean ForwardMensagem(Msg msg){
        Socket source,dest;
        if(users.contains(msg.getRecebe())){ // verifica se está no servidor
            dest = mapSockets.get(msg.getRecebe()).getSocket();
            try {
                ObjectOutputStream pout = mapSockets.get(msg.getRecebe()).getOout();
                pout.writeObject(msg);
                pout.flush();
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
        }

        return false;
    }

    public Database getDatabase(){
        Database database = new Database();
        database.setUsers(this.db.getConnectedUsers());
        Map<String,List<Msg>> msgs = new HashMap<>();
        for(String user : database.getUsers()){
            msgs.put(user,getUserMsgs(user));
        }
        database.setMsgs(msgs);
        return database;
    }

    public List<Msg> getUserMsgs(String user){
        Map<String,String> mapa = db.getUserMsgs(user);
        List<Msg> list = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        strings.addAll(mapa.keySet());
        List<String> values = new ArrayList<>();
        values.addAll(mapa.values());
        for(int i=0;i<strings.size();i++){
            Msg m = new Msg();
            m.setTexto(strings.get(i));
            m.setRecebe(values.get(i));
            list.add(m);
        }
        return list;
    }

    public void shutdown() throws SQLException {
        db.termina();
        this.db.shutdown();
    }

    public void sai() {
        db.termina();
    }
}