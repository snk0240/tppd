package Client;

import Dados.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClienteComm extends Thread {

    private InetAddress ip_server;
    private int udp_port_server;
    private int tcp_port_server;
    private Socket socket;
    private DatagramSocket datagramSocket;

    private ObjectInputStream oin;
    private ObjectOutputStream oout;

    private TransfereThread transfere;
    private TransferenciaFicheiros transferenciaFicheiros;
    boolean autenticado = false;
    private Database database;
    Utilizador utilizador;

    private byte[] data2 = new byte[1024];

    public ClienteComm(InetAddress ip, int udp, TransferenciaFicheiros transferencia){
        this.ip_server = ip;
        this.udp_port_server = udp;
        this.transferenciaFicheiros = transferencia;
    }

    @Override
    public void run() {
        try {
            this.datagramSocket = new DatagramSocket();

            byte[] data1 = Integer.toString(1).getBytes();
            DatagramPacket sendingPacket = new DatagramPacket(data1, data1.length, ip_server, udp_port_server);
            this.datagramSocket.send(sendingPacket);

            DatagramPacket receivingPacket = new DatagramPacket(data2, data2.length);
            this.datagramSocket.receive(receivingPacket);

            String receivedData = new String(receivingPacket.getData());
            System.out.println("Recived TCP Port Server by UDP and it is " + receivedData.trim());

            this.tcp_port_server = Integer.parseInt(receivedData.trim());
            this.datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Socket socket = new Socket(this.ip_server, this.tcp_port_server);
            this.oin = new ObjectInputStream(socket.getInputStream());
            this.oout = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("TCP Socket created successfully!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ThreadClient tc = new ThreadClient();
        tc.start();
    }

    public void login(Login login)
    {
        try {
            this.oout.writeObject(login);
            this.oout.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void registo(Utilizador utilizador){
        try {
            this.oout.writeObject(utilizador);
            this.oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sai(){
        try {
            this.oout.writeObject("sai");
            this.oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviamensagem(Msg msg){
        try {
            this.oout.writeObject(msg);
            this.oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Utilizador getUtilizador(){
        return this.utilizador;
    }

    public void getDatabase(){
        try{
            this.oout.writeObject("getDatabase");
            this.oout.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void listautilizadores(){
        try {
            oout.writeObject("listaUsers");
            oout.flush();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void mostraDatabase(){
        for(String user: database.getUsers()) {
            System.out.println("user: " + user + "\r");
            System.out.println("utilizadores: " + user + "\r");
            if (database.getMsgs().size() > 0)
                for (int i = 0; i < database.getMsgs().size(); i++) {
                    System.out.print(database.getMsgs().get(i) + "; ");
                }
            System.out.println("canais: \r");
            if (database.getCanais().size() > 0)
                for (int i = 0; i < database.getCanais().size(); i++) {
                    System.out.print(database.getCanais().get(i) + "; ");
                }
            System.out.println("ficheiros: \r");
            if (database.getFicheiros().size() > 0)
                for (int i = 0; i < database.getFicheiros().size(); i++) {
                    System.out.print(database.getFicheiros().get(i) + "; ");
                }
            System.out.println("msgs: \r");
            if (database.getMsgs().size() > 0)
                for (int i = 0; i < database.getMsgs().size(); i++) {
                    System.out.print(database.getMsgs().get(i) + "; ");
                }
        }
    }

    public void shutdown() {
        try {
            this.socket.close();
            this.datagramSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public int getUdp_port_server() {
        return this.udp_port_server;
    }

    public void setUdp_port_server(int udp_port_server) {
        this.udp_port_server = udp_port_server;
    }

    public int getTcp_port_server() {
        return this.tcp_port_server;
    }

    public void setTcp_port_server(int tcp_port_server) {
        this.tcp_port_server = tcp_port_server;
    }

    public TransferenciaFicheiros getTransferenciaFicheiros() {
        return this.transferenciaFicheiros;
    }

    public void setTransferenciaFicheiros(TransferenciaFicheiros transferenciaFicheiros) {
        this.transferenciaFicheiros = transferenciaFicheiros;
    }

    public boolean isAutenticado() {
        return this.autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    public void comecaDownloadsThread(){
        transfere = new TransfereThread(utilizador.getPortoTCP(),this);
        transfere.setDaemon(true);
        transfere.start();
    }

    public void setDatabase(Database d) {
        database=d;
    }

    public class ThreadClient extends Thread {
        public ThreadClient(){ }
        @Override
        public void run() {
            while (true) {
                try {
                    Object obj = oin.readObject();

                    if (obj instanceof Msg) {
                        System.out.println("Recebi objeto Msg\n");
                        System.out.println(((Msg) obj).getTexto());

                    } else if (obj instanceof String) {
                        String str = (String) obj;
                        System.out.println("Recebi objeto String: " + str + '\n');

                        if (str.equalsIgnoreCase("Database Changed")) {
                            System.out.println("A base de dados foi atualizada\n");
                            oout.writeObject(new String("Database Update"));
                        }
                    } else if (obj instanceof Request) {
                        Request request = (Request) obj;
                        //EnviaFicheiroThread enviaFicheiroThread = new EnviaFicheiroThread(request,);
                        //enviaFicheiroThread.setDaemon(true);
                        //enviaFicheiroThread.start();
                    } else if(obj instanceof Boolean)
                    {
                        if((Boolean)obj)
                        {
                            System.out.println("Registo realizado com sucesso!\n");
                            autenticado=true;
                            //comecaDownloadsThread();
                        }
                        else
                            System.out.println("Registo falhado...\n");
                    }
                    else if(obj instanceof Utilizador)
                    {
                        utilizador = (Utilizador) obj;

                        System.out.println("Recebi obj Utilizador!\n");
                        autenticado=true;
                        //comecaDownloadsThread();
                    }
                    else if(obj instanceof ArrayStoreException){
                        System.out.println("O login falhou...\n");
                    }
                    else if(obj instanceof Database){
                        setDatabase((Database)obj);
                    }
                    else if(obj instanceof Map){
                        Map<String, List<String>>mapa = (Map) obj;
                        List<String>keys = new ArrayList<>();
                        keys.addAll(mapa.keySet());
                        if(keys.get(0).equals("utilizadores")){
                            mostraUtilizadores(mapa.get("utilizadores"));
                        }
                        else if(keys.get(0).equals("canais")){
                            mostraFicheiros(mapa.get("canais"));
                        }
                        else if(keys.get(0).equals("ficheiros")){
                            mostraFicheiros(mapa.get("ficheiros"));
                        }
                        else if(keys.get(0).equals("msgs")){
                            mostraFicheiros(mapa.get("msgs"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void mostraUtilizadores(List<String>users){
            for(String s:users){
                System.out.println(s);
            }
        }
        public void mostraFicheiros(List<String> ficheiros){
            for(String s: ficheiros){
                System.out.println(s);
            }
        }
    }
}
