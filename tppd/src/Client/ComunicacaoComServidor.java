package Client;

import Dados.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

// A comunicação com o servidor ao qual o cliente está ligado é por TCP permanente
// Quando existe uma alteração por parte dum utilizador ligado a outro servidor,
// é necessária uma ligação UDP temporária para notificar os mesmos.

public class ComunicacaoComServidor extends Observable {

    Socket socketServidor;
    boolean autenticado=false;
    DatagramSocket datagramSocket;
    Utilizador utilizador;
    ServidorThread servidorThread;
    TransfereThread transfereThread;
    DatabaseUpdate updatedDb;
    ObjectOutputStream oout;
    ObjectInputStream ois;
    TransferenciaFicheiros transferenciaFicheiros;
    Database database;
    //List <Observer> l = new ArrayList<>();

    public ComunicacaoComServidor(TransferenciaFicheiros transferenciaFicheiros){
        //this.socketServidor = socketServidor;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //updatedDb = new DatabaseUpdate();
        this.transferenciaFicheiros = transferenciaFicheiros;
    }

    public void comecaThread(){
        servidorThread = new ServidorThread(socketServidor,this,oout,ois);
        servidorThread.setDaemon(true);
        servidorThread.start();
    }
    public void comecaDownloadsThread(){
        transfereThread = new TransfereThread(utilizador.getPortoTCP(),this);
        transfereThread.setDaemon(true);
        transfereThread.start();
    }
    public void downloadficheiro(FileRequest fr){
        try {
            //oout.reset();
            oout.writeObject(fr);
            oout.flush();
        } catch (IOException | NullPointerException  e) {
            e.printStackTrace();
        }
    }
    public DatagramSocket getDatagramSocket(){
        return datagramSocket;
    }
    public void enviamensagem(Msg msg){
        try {
            oout.reset();
            oout.writeObject(msg);
            oout.flush();

        } catch (IOException | NullPointerException  e) {
            e.printStackTrace();
        }
    }
    public void registo(Utilizador utilizador){
        Boolean autenticado = false;
        try {
            //oout.reset();
            oout.writeObject(utilizador);
            oout.flush();
            //ois = new ObjectInputStream(socketServidor.getInputStream());
            //autenticado = ois.readBoolean();
        } catch (IOException | NullPointerException  e) {
            e.printStackTrace();
        }
    }

    public List<File> getFilesList(String path){
        File folder = new File(path);
        if(!folder.isDirectory()){
            return null;
        }
        List<File> list =new ArrayList<>();
        for( File f : folder.listFiles()){
            if(!f.isDirectory()) {
                list.add(f);
            }
        }
        return list;
    }
    public Map<String,Long> getFilesMap(List<File> files){
        Long tamanho;
        String nome;
        Map<String,Long> mapa = new HashMap<>();
        for(File f : files){
            tamanho = f.length();
            nome = f.getName();
            mapa.put(nome,tamanho);
        }
        return mapa;
    }
    public DatabaseUpdate createSendableMap(String username, Map<String,Long> mapaFinal){
        Map<String,Map<String,Long>> novoMapa = new HashMap<>();

        novoMapa.put(username,mapaFinal);
        DatabaseUpdate update = new DatabaseUpdate();
        update.setData(novoMapa);
        return update;
    }
    public void updateDatabase(){
        DatabaseUpdate db = createSendableMap(utilizador.getUsername(),
                getFilesMap(getFilesList(utilizador.getCaminhoEnviar())));
        try{
            oout.reset();
            oout.writeObject(db);
            oout.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void getDatabase(){
        try{
            oout.writeObject("getDatabase");
            oout.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void setDatabase(Database d){
        database=d;
        setChanged();
        notifyObservers(d);
    }
    /*public void mostraDatabase(){
        for(String user: database.getUsers()){
            System.out.println("user: "+user+"\r");
            for(int i=0;i<database.getUserFile(user).size();i++){
                System.out.print(database.getUserFile(user).get(i)+"; ");
            }
            System.out.println("donwloads: \r");
            for(int i=0;i<database.getUserDownloads(user).size();i++){
                System.out.print(database.getUserDownloads(user).get(i)+"; ");
            }
            System.out.println("uploads: \r");
            for(int i=0;i<database.getUserUploads(user).size();i++){
                System.out.print(database.getUserUploads(user).get(i)+"; ");
            }
        }
    }*/
    public void login(Login login)
    {
        Object obj;
        try {
            //oout.reset();
            oout.writeObject(login);
            oout.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void listautilizadores(){
        /*List<String> list = new ArrayList<>();
        list.addAll( updatedDb.getData().keySet());
        for (int i = 0; i < list.size()-1; i++) {
            if(i == 0)
                System.out.println("Utilizadores Ligados:\n");
            System.out.println(i+1 + ": " + list.get(i));
        }*/
        try {
            oout.writeObject("listaUsers");
            oout.flush();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public void listaficheiros(){
        /*List<String> listautilizadores = new ArrayList<>();
        List<Map<String,Long>> listaficheiros = new ArrayList<>();
        List<String> nomeficheiros = new ArrayList<>();
        List<Long> tamanhoficheiros = new ArrayList<>() ;

        listaficheiros.addAll(updatedDb.getData().values());
        listautilizadores.addAll( updatedDb.getData().keySet());
        for (int i = 0; i < listautilizadores.size()-1; i++) {
            if(i == 0)
                System.out.println("Lista de Ficheiros:\n");
            if(listaficheiros.get(i).isEmpty())
                continue;
            else
            {
                System.out.println("Ficheiros do utilizador " + listaficheiros.get(i) +
                        ":\nNomeFicheiro | Tamanho");
                nomeficheiros = new ArrayList<>(listaficheiros.get(i).keySet());
                tamanhoficheiros = new ArrayList<>(listaficheiros.get(i).values());
            }
            for(int j = 0; j < nomeficheiros.size()-1;j++)
            {
                System.out.println(nomeficheiros.get(j) + " | " + tamanhoficheiros.get(j) + '\n');
            }
        }*/
        Scanner s = new Scanner(System.in);
        String user="";
        try{
            user=s.nextLine();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            oout.writeObject(user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void ReceivedFileRequest(Request r){
        //transferenciaFicheiros.LoadFile(r.getFilename(),r.getIp(),r.getPorto(),utilizador.getCaminhoEnviar());
        try{

        }catch(Exception e){

        }
    }
    public Utilizador getUtilizador(){
        return utilizador;
    }

}
