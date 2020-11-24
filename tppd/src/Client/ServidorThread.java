package Client;

import Dados.*;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServidorThread extends Thread {     //Thread para tratar a comunicação com o Servidor
    Socket socket;
    ComunicacaoComServidor comunicacaoComServidor;
    ObjectOutputStream oout;
    ObjectInputStream oin;

    public ServidorThread(Socket s,ComunicacaoComServidor comunicacaoComServidor,ObjectOutputStream oout,ObjectInputStream oin){
        socket =s;
        this.comunicacaoComServidor=comunicacaoComServidor;
        this.oout = oout;
        this.oin = oin;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Object obj = oin.readObject();

                if (obj instanceof Msg) {
                    System.out.println("Recebi objeto Msg\n");
                    System.out.println(((Msg) obj).getMensagem());

                } else if (obj instanceof String) {
                    String str = (String) obj;
                    System.out.println("Recebi objeto String: " + str + '\n');

                    if (str.equalsIgnoreCase("KeepAlive")) {
                        oout.writeObject(new String("ack"));
                    } else if (str.equalsIgnoreCase("Database Changed")) {
                        System.out.println("A base de dados foi atualizada\n");
                        oout.writeObject(new String("Database Update"));
                    }
                } else if (obj instanceof Request) {
                    Request request = (Request) obj;
                    EnviaFicheiroThread enviaFicheiroThread = new EnviaFicheiroThread(request,comunicacaoComServidor);
                    enviaFicheiroThread.setDaemon(true);
                    enviaFicheiroThread.start();
                } else if(obj instanceof DatabaseUpdate)
                {
                    comunicacaoComServidor.updatedDb = (DatabaseUpdate) obj;
                    System.out.println("A minha base de dados interna foi atualizada\n");
                }
                else if(obj instanceof Boolean)
                {
                    if((Boolean)obj)
                    {
                        System.out.println("Registo realizado com sucesso!\n");
                        comunicacaoComServidor.autenticado=true;
                        comunicacaoComServidor.comecaDownloadsThread();
                    }
                    else
                        System.out.println("Registo falhado...\n");
                }
                else if(obj instanceof Utilizador)
                {
                    comunicacaoComServidor.utilizador = (Utilizador) obj;
                    List<File> files = comunicacaoComServidor.getFilesList(comunicacaoComServidor.utilizador.getCaminhoEnviar());

                    System.out.println("Recebi obj Utilizador!\n");
                    comunicacaoComServidor.autenticado=true;
                    comunicacaoComServidor.comecaDownloadsThread();
                }
                else if(obj instanceof ArrayStoreException){
                        System.out.println("O login falhou...\n");
                }
                else if(obj instanceof Database){
                    comunicacaoComServidor.setDatabase((Database)obj);
                }
                else if(obj instanceof Map){
                    Map<String,List<String>>mapa = (Map) obj;
                    List<String>keys = new ArrayList<>();
                    keys.addAll(mapa.keySet());
                    if(keys.get(0).equals("utilizadores")){
                        mostraUtilizadores(mapa.get("utilizadores"));
                    }
                    else if(keys.get(0).equals("ficheiros")){
                        mostraFicheiros(mapa.get("ficheiros"));
                    }
                }
                //oout.reset();
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
